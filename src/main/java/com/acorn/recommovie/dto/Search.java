package com.acorn.recommovie.dto;

import lombok.Data;


public class Search {
	// extends Pagination
    private String searchType;
    private String searchKey;
    
    public String getSearchType() {
        return searchType;
    }
    
    public void setSearchType(String searchType) {
        this.searchType=searchType;
    }
    
    public String getSearchKey() {
        return searchKey;
    }
    
    public void setSearchKey(String searchKey) {
        this.searchKey=searchKey;
    }
}
