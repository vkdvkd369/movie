package com.acorn.recommovie.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acorn.recommovie.dto.Genre;
import com.acorn.recommovie.dto.Movie;
import com.acorn.recommovie.mapper.MoviesMapper;

@Controller 
@RequestMapping("/recommend")
public class SearchController {
    @RequestMapping(value="/getSearchList", method=RequestMethod.GET)
    public String getSearchList(Model model
            , @RequestParam(required=false, defaultValue="title") String searchType
            , @RequestParam(required=false) String searchKey
            ) throws Exception{
        
        Search search=new Search();
        Search.setSearchType(searchType);
        Search.setSearchKey(searchKey);
        
        model.addAttribute("pagination", search);
        model.addAttribute("searchList", searchService.getSearchList(search));
        return "search/searchList";
    }


}



