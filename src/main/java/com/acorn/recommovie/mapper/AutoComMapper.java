package com.acorn.recommovie.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.acorn.recommovie.dto.Person;

@Mapper
public interface AutoComMapper {
	List<String> autocompleteByTitle(String movieTitle) throws Exception;
	List<String> autocompleteByName(String personName) throws Exception;
}
