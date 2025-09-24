package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

public class Comments {
    @Schema(description = "Общее количество комментариев")
    private  Integer count;

    private ArrayList<Comment> results;

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

    public ArrayList<Comment> getResults() {
        return results;
    }

    public void setResults(ArrayList<Comment> results) {
        this.results = results;
    }
}
