package com.acorn.recommovie.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/recommend")
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
	
	//검색된 목록 중 선택된 영화들의 Movie DTO가 list로 넘어오도록 함
	@PostMapping("resultSelect.do")
	public String sentimentAnalysis(List<Movie> selectedMovies) {
		HashMap<Movie,List<String>> movieReviews = new HashMap<Movie,List<String>>();
		for (Movie movie : selectedMovies) {
			String URL = "https://movie.naver.com/movie/bi/mi/point.naver?code="+Integer.toString(movie.getMovieCode());
			Document mainPage = null;
			try { mainPage = Jsoup.connect(URL).get(); } catch (IOException e) {e.printStackTrace();}
			
			List<String> reviews = new ArrayList<String>();
			Elements aTags = mainPage.select(".paging > div > a");
			for(Element a : aTags) {
				Document page = null;
				try {
					page = Jsoup.connect(a.attr("href")).get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Elements spanTags = page.select(".score_reple > p > span");
				for(Element el : spanTags) {
					reviews.add(el.text());
				}
			}
			
			movieReviews.put(movie, reviews);
		}
		
		System.out.println("Review data 수집 완료");
		
		RestTemplate restTemplate = new RestTemplate();
//		restTemplate.postForObject("http://localhost:8081/sentiment/predict", list<)
		
		// to result
		return null;
	}
	

	
	@GetMapping("OptionDetail")
	
	public void OptionDetail() {}
	
	@GetMapping("result")
	
	public void result() {}
	
	@GetMapping("similarResult")
	
	public void similarResult() {}
	
	@GetMapping("gridView")
	
	public void gridView() {}
}
