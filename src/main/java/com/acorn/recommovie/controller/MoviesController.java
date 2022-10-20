package com.acorn.recommovie.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.acorn.recommovie.dto.Genre;
import com.acorn.recommovie.dto.Movie;
import com.acorn.recommovie.mapper.MoviesMapper;

@Controller 
@RequestMapping(value="/recommend", produces="text/plain;charset=UTF-8")
public class MoviesController {

	@Autowired
	private MoviesMapper moviesMapper;
	
	
	
	
	@GetMapping("rangeSelect")
	public String rangeSelect(Model model) {

		List<Genre> allGenre = null;
		try {
			allGenre = moviesMapper.selectAllGenre();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("allGenre", allGenre);
		System.out.println(allGenre);
		return "recommend/rangeSelect";
	}
	
	@PostMapping("rangeSelect.do")
	public String rangeSelect(@RequestParam String movie_keyword, Model model) {
		List<Movie> movies = null;
		try {
			movies = moviesMapper.selectMovieByTitle(movie_keyword);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// 검색된 모든 영화에 대한 썸네일 이미지URL을 가져옴
		HashMap<Integer,String> thumbURLs = new HashMap<Integer,String>(); 
	
		for (Movie movie : movies) {
			String moviePageURL = "https://movie.naver.com/movie/bi/mi/basic.naver?code="+Integer.toString(movie.getMovieCode());
			
			Document page = null;
			try {page = Jsoup.connect(moviePageURL).get();} catch (IOException e) {e.printStackTrace();}
			
			Elements thumbImg = page.select(".poster > a > img");
			String thumbURL=thumbImg.attr("src");
			thumbURLs.put(movie.getMovieId(), thumbURL);
		}
		
		model.addAttribute("thumbURLs", thumbURLs);
		model.addAttribute("movies",movies);

		// to resultSelect
		return "recommend/list";
	}

	@GetMapping("resultSelect")
	public void resultSelect() {}
	
	//검색된 목록 중 선택된 영화들의 Movie DTO가 list로 넘어오도록 함
	@PostMapping("resultSelect.do")
	public String sentimentAnalysis(List<Movie> selectedMovies, Model model) {
		// test mapping
		//List<Movie> selectedMovies = moviesMapper.selectMovieByTitle("포켓몬");

		HashMap<Integer,Object> movieReviews = new HashMap<>();
		for (Movie movie : selectedMovies) {
			String URL = "https://movie.naver.com/movie/point/af/list.naver?st=mcode&sword="+Integer.toString(movie.getMovieCode())+"&target=after";
			Document mainPage = null;
			try { mainPage = Jsoup.connect(URL).get(); } catch (IOException e) {e.printStackTrace();}
			
			int reviewNum = Integer.parseInt(mainPage.select(".c_88").text());
			List<String> reviews = new ArrayList<String>();
			String pageURL = URL+"&page=";
			for(int i = 1; i <= reviewNum/10; i++) {
				Document page = null;
				try { page = Jsoup.connect(pageURL+Integer.toString(i)).get(); } catch (IOException e) {e.printStackTrace();}
				Elements reviewElements = page.select(".title");
				for (Element reviewElement : reviewElements) {
					String review = reviewElement.ownText();
					reviews.add(review);
				}
			}
			
			movieReviews.put(movie.getMovieId(), reviews);
		}
		
		System.out.println("Review data 수집 완료");

		System.out.println("수집된 영화 개수 :"+movieReviews.size());

		RestTemplate restTemplate = new RestTemplate();
		String apiURL ="http://localhost:8081/sentiment/predict";
		
		//post request to sentiment analysis server
		ResponseEntity<String> response = restTemplate.postForEntity(apiURL, movieReviews, String.class);
		String result = response.getBody();
		
		Gson gson = new Gson();
		Map<String, Object> resultMap = gson.fromJson(result, Map.class);
		
		System.out.println("감성분석 결과 수신 완료");
		System.out.println(resultMap);

		model.addAttribute("resultMap", resultMap);
		return "recommend/result";

	}

	@GetMapping("result")
	public void result() {}

	@GetMapping("similarResult")
	public void similarResult() {}

	@PostMapping("similarResult.do")
	public String similarAnalysis(@RequestParam int movieId, Model model){
		String movieStory = moviesMapper.selectMovieStoryById(movieId);
		RestTemplate restTemplate = new RestTemplate();
		String apiURL ="http://localhost:8081/similar/predict";
		ResponseEntity<String> response = restTemplate.postForEntity(apiURL, movieStory, String.class);
		Gson gson = new Gson();
		Map<String, Object> resultMap = gson.fromJson(response.getBody(), Map.class);
		System.out.println(resultMap);
		
		model.addAttribute("resultMap", resultMap);
		return "recommend/similarResult";
	}
	

}
