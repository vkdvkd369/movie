package com.acorn.recommovie.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

import org.apache.ibatis.session.SqlSessionException;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.acorn.recommovie.dto.Genre;
import com.acorn.recommovie.dto.Movie;
import com.acorn.recommovie.dto.Person;
import com.acorn.recommovie.mapper.MoviesMapper;
import java.io.FileWriter;
import java.io.FileReader;

@Controller 
@RequestMapping(value="/recommend", produces="text/plain;charset=UTF-8")
public class MoviesController {

	@Autowired
	private MoviesMapper moviesMapper;
	
	@Transactional
	@GetMapping("UpdateOnAirMovies")
	public String updateOnAirMovies(){
		Document airpage=null;
		try {
			airpage=Jsoup.connect("https://movie.naver.com/movie/running/current.naver").get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Elements airingList = airpage.select("ul.lst_detail_t1 > li > div > a");
		List<Integer> airingCodes = new ArrayList<>();
		List<Integer> havetoUpdateCodes = new ArrayList<>();
		for(Element e : airingList){
			airingCodes.add(Integer.parseInt(e.attr("href").split("=")[1]));
		}
		Map<Integer,Integer> movieCodeId = moviesMapper.selectMoviesIdByCodeList(airingCodes);
		for(Integer code : airingCodes){
			if(movieCodeId.get(code)==null){
				havetoUpdateCodes.add(code);
			}
		}
		if(havetoUpdateCodes.size()>0){
			List<Movie> havetoUpdateMovies = new ArrayList<Movie>();
			for(Integer code : havetoUpdateCodes){
				Movie movie = new Movie();
				Document moviepage = null;
				try {
					moviepage=Jsoup.connect("https://movie.naver.com/movie/bi/mi/basic.naver?code="+code).get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(moviepage != null){
					String movieTitle = moviepage.selectFirst("h3.h_movie a").text();
					String movieStory = moviepage.selectFirst("div.story_area p.con_tx").text();
					String moviePoster = moviepage.selectFirst("div.poster img").attr("src");
					ArrayList<Genre> movieGenre = new ArrayList<Genre>();
					for(Element e : moviepage.select("dl.info_spec dd:nth-child(2) a")){
						for(Genre g : moviesMapper.selectAllGenres()){
							if(e.text().equals(g.getGenreName())){
								movieGenre.add(g);
							}
						}
					}

					ArrayList<Person> moviePeople = new ArrayList<Person>();
					for(Element e : moviepage.select("dl.info_spec dd:nth-child(6) a")){
						Person person = new Person();
						person.setPersonName(e.text());
						moviePeople.add(person);
					}
					for(Element e : moviepage.select("dl.info_spec dd:nth-child(4) a")){
						Person person = new Person();
						person.setPersonName(e.text());
						moviePeople.add(person);
					}

					movie.setMovieCode(code);
					movie.setMovieTitle(movieTitle);
					movie.setMovieStory(movieStory);
					movie.setPeople(moviePeople);
					movie.setGenres(movieGenre);
					havetoUpdateMovies.add(movie);
				}			
			}		
			
			updateMoviesToDb(havetoUpdateMovies);
			System.out.println("???????????? ??????");
			System.out.println("??????????????? ?????? ??? : "+havetoUpdateMovies.size());
			System.out.println("??????????????? ?????? ?????? : "+havetoUpdateMovies);
		}
		return "redirect:/";

		
	}

	@Transactional
	public void updateMoviesToDb(List<Movie> movies) throws SqlSessionException{
		for(Movie movie : movies){
			moviesMapper.insertMovie(movie);
			for(Person person : movie.getPeople()){
				moviesMapper.insertPerson(person);
				moviesMapper.insertMoviePerson(movie.getMovieId(), moviesMapper.selectPersonByName(person.getPersonName()).getPersonId());
			}
			for(Genre genre : movie.getGenres()){
				moviesMapper.insertMovieGenre(movie.getMovieId(), moviesMapper.selectGenreIdByName(genre.getGenreName()));

			}
		}
	}
	
	
	@GetMapping("rangeSelect")
	public String rangeSelect(Model model) {

		List<Genre> allGenre = null;
		try {
			allGenre = moviesMapper.selectAllGenres();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("allGenre", allGenre);
		return "recommend/rangeSelect";
	}
	
	@PostMapping("rangeSelect.do")
	public String rangeSelect(
			@RequestParam(required = false) boolean chkAir, 
			@RequestParam(required = false) String movieKeyword,
			@RequestParam(required = false) String personName,
			@RequestParam(required = false) Integer genreId,
			Model model) {
		System.out.println(chkAir+"\n"+movieKeyword+"\n"+ personName +"\n"+ String.valueOf(genreId));

		Document airpage = null;
		
		List<Movie> movies = null;
		try {
			movies = moviesMapper.selectMovies(movieKeyword, personName, genreId);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// ????????? ?????? ????????? ?????? ????????? ?????????URL??? ?????????
		HashMap<Integer,String> thumbURLs = new HashMap<Integer,String>(); 
	
		for (Movie movie : movies) {
			String moviepageURL = "https://movie.naver.com/movie/bi/mi/basic.naver?code="+Integer.toString(movie.getMovieCode());
			
			Document page = null;
			try {page = Jsoup.connect(moviepageURL).get();} catch (IOException e) {e.printStackTrace();}
			
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
		//List<Movie> selectedMovies = moviesMapper.selectMovieByTitle("?????????");
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
		
		System.out.println("Review data ?????? ??????");

		System.out.println("????????? ?????? ?????? :"+movieReviews.size());

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

		System.out.println("???????????? ?????? ?????? ??????");
		List<HashMap<String,Object>> sendMovies = new ArrayList<>();
		for(List<Object> li : (List<List<Object>>)resultMap.get("repleMovie")){
			for (Movie movie : selectedMovies){
				if (Integer.parseInt((String)li.get(0))==movie.getMovieId()){
					HashMap<String,Object> send = new HashMap<>();
					send.put("movieId",movie.getMovieId());
					send.put("movieTitle", movie.getMovieTitle());
					send.put("positiveRatio",(Double)(li.get(1)));

					String moviepageURL = "https://movie.naver.com/movie/bi/mi/basic.naver?code="+Integer.toString(movie.getMovieCode());
					Document page = null;
					try {page = Jsoup.connect(moviepageURL).get();} catch (IOException e) {e.printStackTrace();}
					
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
	public void similarResult() {}

	@PostMapping("similarResult.do")
	public String similarAnalysis( /* @RequestParam int movieId, */ Model model){
		//test mapping
		int movieId = 25401;
		
		
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
			Double M_id = (Double) entry.getValue().get("id");
			int id = M_id.intValue();
			Movie movie = moviesMapper.selectMovieByMId(id);
			sendsub.put("movie", movie);
			sendsub.put("score", entry.getValue().get("score"));
			List<String> movieGenres=moviesMapper.selectGenreNameById(id);
			sendsub.put("genres", movieGenres);

			
			String moviePageURL = "https://movie.naver.com/movie/bi/mi/basic.naver?code="+Integer.toString(movie.getMovieCode());
			Document page = null;
			try {page = Jsoup.connect(moviePageURL).get();} catch (IOException e) {e.printStackTrace();}
			
			Elements thumbImg = page.select(".poster > a > img");
			String thumbURL=thumbImg.attr("src");
			sendsub.put("thumbURL", thumbURL);
			send.put(entry.getKey(), sendsub);
		}
		
		model.addAttribute("send", send);
		System.out.println(send);
		// send ??????: { "index" : {"score":0.918301105160158, "movie": moviedto } }
		return "recommend/similarResult";
		
	}

}
