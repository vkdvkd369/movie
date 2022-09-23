package com.example.movierecomm.dto;

import lombok.Data;
//+------------+--------------+------+-----+---------+----------------+
//| Field      | Type         | Null | Key | Default | Extra          |
//+------------+--------------+------+-----+---------+----------------+
//| movieId    | int          | NO   | PRI | NULL    | auto_increment |
//| movieTitle | varchar(100) | NO   |     | NULL    |                |
//+------------+--------------+------+-----+---------+----------------+


@Data
public class movies {
	private int movieId;
	private String movieTitle;
}
