package com.acorn.recommovie.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.acorn.recommovie.dto.Person;

@Mapper
public interface AutoComMapper {
	List<String> autocompleteByTitle(@Param("keyword")String keyword) throws Exception;
	List<Person> autocompleteByPeople(String personName) throws Exception;
}
