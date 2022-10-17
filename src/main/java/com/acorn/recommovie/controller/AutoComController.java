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
	
	@PostMapping(value = "/ajax/autocomplete.do")
	public @ResponseBody List<String> autocomplete
    						(String keyword) throws Exception{

		if(keyword ==null)
			return null;
		else {
			List<String> resultList = autoComMapper.autocompleteByTitle(keyword);
			return resultList;
		}
			
		
		
		
	}
	
}