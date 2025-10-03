package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class Ads {
    @Schema(description = "Общее количество объявлений")
    private  Integer count;

    private List<Ad> results;

    public Ads() {
    }

    public Ads(Integer count, ArrayList<Ad> results) {
        this.count = count;
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Ad> getResults() {
        return results;
    }

    public void setResults(List<Ad> results) {
        this.results = results;
    }
}
