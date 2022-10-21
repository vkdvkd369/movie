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
import java.io.FileWriter;
import java.io.FileReader;

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
		System.out.println("movies : "+movies);
		System.out.println("thumbURLs : "+thumbURLs);

		// to resultSelect
		// return "recommend/resultSelect";
		return "recommend/resultSelect";
	}

	@GetMapping("resultSelect")
	public void resultSelect() {}
	
	//get form data not using dto by list
	@PostMapping("resultSelect.do")
	public String sentimentAnalysis(@RequestParam String[] movieId,Model model ) throws IOException {
		//test mapping
		//List<Movie> selectedMovies = moviesMapper.selectMovieByTitle("포켓몬");
		List<Movie> selectedMovies = new ArrayList<Movie>();

		for(String id : movieId) {
			
			selectedMovies.add(moviesMapper.selectMovieById(Integer.parseInt(id)));
		}
		

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
		
		FileWriter fw = new FileWriter("testData");
		fw.write(result);
		fw.flush();
		fw.close();

		FileReader fr = new FileReader("testData");
		Gson gson = new Gson();
		Map<String, Object> resultMap = gson.fromJson(fr, Map.class);
		fr.close();
		// System.out.println(resultMap);

		System.out.println("감성분석 결과 수신 완료");
		List<HashMap<String,Object>> sendMovies = new ArrayList<>();
		for(List<Object> li : (List<List<Object>>)resultMap.get("repleMovie")){
			for (Movie movie : selectedMovies){
				if (Integer.parseInt((String)li.get(0))==movie.getMovieId()){
					HashMap<String,Object> send = new HashMap<>();
					send.put("movieId",movie.getMovieId());
					send.put("movieTitle", movie.getMovieTitle());
					send.put("positiveRatio",(Double)(li.get(1)));

					String moviePageURL = "https://movie.naver.com/movie/bi/mi/basic.naver?code="+Integer.toString(movie.getMovieCode());
					Document page = null;
					try {page = Jsoup.connect(moviePageURL).get();} catch (IOException e) {e.printStackTrace();}
					
					Elements thumbImg = page.select(".poster > a > img");
					String thumbURL=thumbImg.attr("src");
					send.put("movieThumbUrl", thumbURL);
					
						
					sendMovies.add(send);
				};
			
			}
		}
		// System.out.println(sendMovies);
		
		model.addAttribute("rst", sendMovies);

		return "recommend/result";

	}

	@GetMapping("test")
	public void test(){}

	@GetMapping("result")
	public void result() {}

	@GetMapping("similarResult")
	public void similarResult(){}

	@PostMapping("similarResult.do")
	public String similarAnalysis(/* @RequestParam int movieId, */ Model model){
		//test mapping
		int movieId = 16995;

		String movieStory = moviesMapper.selectMovieStoryById(movieId);
		System.out.println(movieStory);
		RestTemplate restTemplate = new RestTemplate();
		String apiURL ="http://localhost:8081/similar/predict";
		HashMap<String,Object> request = new HashMap<>();
		request.put("movieStory",movieStory);
		ResponseEntity<String> response = restTemplate.postForEntity(apiURL, request, String.class);

		
		Gson gson = new Gson();
		Map<String, Map<String,Object>> resultMap = gson.fromJson(response.getBody(), Map.class);
		System.out.println(resultMap);
		HashMap<String, Map<String,Object>> send = new HashMap<>();
		for(Map.Entry<String,Map<String, Object>> entry : resultMap.entrySet()){
			HashMap<String,Object> sendsub = new HashMap<>();
			Movie movie = moviesMapper.selectMovieByTitleEqual(entry.getValue().get("title").toString());
			sendsub.put("movie", movie);
			sendsub.put("score", entry.getValue().get("score"));
			sendsub.put("thumbURL", "https://movie.naver.com/movie/bi/mi/photoViewPopup.naver?movieCode="+movie.getMovieCode());
			send.put(entry.getKey(), sendsub);
		}
		
		model.addAttribute("send", send);;
		// send 형식: { "index" : {"score":0.91830110516015, "movie": moviedto } }
		return "recommend/similarResult";
	}
}
