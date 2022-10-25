package com.acorn.recommovie.dto;

import java.util.ArrayList;

import lombok.Data;
//+------------+--------------+------+-----+---------+----------------+
//| Field      | Type         | Null | Key | Default | Extra          |
//+------------+--------------+------+-----+---------+----------------+
//| movieId    | int          | NO   | PRI | NULL    | auto_increment |
//| movieTitle | varchar(100) | NO   |     | NULL    |                |
//| movieCode  | int          | YES  |     | NULL    |                |
//| movieYear  | int          | YES  |     | NULL    |                |
//| movieStory | mediumtext   | YES  |     | NULL    |                |
//+------------+--------------+------+-----+---------+----------------+



@Data
public class Movie {
	private int movieId;
	private String movieTitle;
	private int movieCode;
	private int movieYear;
	
	private ArrayList<Genre> genres;
	private ArrayList<Person> people;
	private String movieStory;

	public void setGenres(ArrayList<Genre> genres) {
		this.genres = genres;
	}
	public void setPeople(ArrayList<Person> people) {
		this.people = people;
	}
}
