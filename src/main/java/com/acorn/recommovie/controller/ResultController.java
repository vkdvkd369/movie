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
@RequestMapping("/")
public class ResultController {
	
}