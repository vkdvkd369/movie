package com.acorn.recommovie.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.acorn.recommovie.dto.Genre;
import com.acorn.recommovie.dto.Movie;

@Mapper
public interface MoviesMapper {
	 List<Movie> selectAllMovie();
	 List<Movie> selectMovie(String movieTitle, String personName, Integer genreId);
	 Movie selectMovieById(int movieId);
	 List<Genre> selectAllGenre();
	 String selectMovieStoryById(int movieId);
	 Movie selectMovieByTitleEqual(String movieTitle);
}

