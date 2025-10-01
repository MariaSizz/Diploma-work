package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;

public class CreateOrUpdateComment {
    @Schema(description = "Текст комментария")
    @Size(min = 1, max = 1024, message = "Текст должен быть между 1 and 1024 символами")
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
