package com.acorn.recommovie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/Option")
@Controller
public class OptionController {
	@GetMapping("OptionDetail")
	
	public void OptionDetail() {}
	
	@GetMapping("result")
	
	public void result() {}
	
	@GetMapping("similarResult")
	
	public void similarResult() {}
	
	@GetMapping("gridView")
	
	public void gridView() {}

}
