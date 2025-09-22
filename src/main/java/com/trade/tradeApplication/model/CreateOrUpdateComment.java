package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class CreateOrUpdateComment {
    @Schema(description = "Текст комментария")
    private String text;



    public CreateOrUpdateComment() {
    }

    public CreateOrUpdateComment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
