package com.acorn.recommovie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.acorn.recommovie.dto.Genre;
import com.acorn.recommovie.dto.Movie;
import com.acorn.recommovie.dto.Person;

@Mapper
public interface MoviesMapper {
	 List<Movie> selectAllMovies();
	 List<Movie> selectMovies(String movieTitle, String personName, Integer genreId);
	 Movie selectMovieById(int movieId);
	 List<Genre> selectAllGenres();
	 int selectGenreIdByName(String genreName);
	 String selectMovieStoryById(int movieId);
	 Movie selectMovieByTitleEqual(String movieTitle);
	 Map<Integer,Integer> selectMoviesIdByCodeList(List<Integer> movieCodeList);
	 Person selectPersonByName(String personName);
	 
	//test용
	 List<String> selectGenreNameById(int movieId);
	 Movie selectMovieByMId(int movieId);


	 
	 void insertMovie(Movie movie);
	 void insertPerson(Person person);
	 void insertMovieGenre(Integer movieId, Integer genreId);
	 void insertMoviePerson(Integer movieId, Integer personId);
}

