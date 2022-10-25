package com.acorn.recommovie.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.acorn.recommovie.dto.Genre;
import com.acorn.recommovie.dto.Movie;

@Mapper
public interface MoviesMapper {
	 List<Movie> selectAllMovies();
	 List<Movie> selectMovies(String movieTitle, String personName, Integer genreId);
	 Movie selectMovieById(int movieId);
	 List<Genre> selectAllGenres();
	 String selectMovieStoryById(int movieId);
	 Movie selectMovieByTitleEqual(String movieTitle);
	 
	 String selectTitleById(int movieId);
	 int selectIdByTitle(String movieTitle);
}

