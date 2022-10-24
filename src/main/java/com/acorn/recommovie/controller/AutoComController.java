package com.acorn.recommovie.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.acorn.recommovie.mapper.AutoComMapper;

@RestController
public class AutoComController {

	@Autowired
	AutoComMapper autoComMapper;
	
	@PostMapping(value = "/ajax/autocompleteTitle.do")
	public @ResponseBody List<String> autocompleteTitle
    						(String keyword) throws Exception{

		if(keyword ==null)
			return null;
		else {
			List<String> resultList = autoComMapper.autocompleteByTitle(keyword);
			return resultList;
		}
			
	}
	
	@PostMapping("/ajax/autocompleteName.do")
	public @ResponseBody List<String> autocompleteName(String keyword) throws Exception{
		if(keyword ==null)
			return null;
		else {
			List<String> resultList = autoComMapper.autocompleteByName(keyword);
			return resultList;
		}
	}
	
}