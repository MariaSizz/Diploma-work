package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class Comments {
    @Schema(description = "Общее количество комментариев")
    private  Integer count;

    private List<Comment> results;

    public Comments() {
    }

    public Comments(Integer count, ArrayList<Comment> results) {
        this.count = count;
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Comment> getResults() {
        return results;
    }

    public void setResults(List<Comment> results) {
        this.results = results;
    }
}
