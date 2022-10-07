package com.acorn.recommovie.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		HashMap<Integer,String> thumbStrs = new HashMap<Integer,String>(); 
		for (Movie movie : movies) {
			String moviePageURL = "https://movie.naver.com/movie/bi/mi/basic.naver?code="+Integer.toString(movie.getMovieCode());
			System.out.println(moviePageURL);
			Document page = null;
			try {
				page = Jsoup.connect(moviePageURL).get();

			} catch (IOException e) {
				e.printStackTrace();
			}
			Elements thumbImg = page.select(".poster > a > img");
			String thumbStr=thumbImg.attr("src");
			thumbStrs.put(movie.getMovieId(), thumbStr);
		}
		
		model.addAttribute("thumbStrs", thumbStrs);
		model.addAttribute("movies",movies);

		/* System.out.println(movies); */
		return "recommend/list";
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
