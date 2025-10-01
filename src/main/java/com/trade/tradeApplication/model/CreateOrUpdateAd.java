package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateOrUpdateAd {

    @NotNull(message = "Значение заголовка объявления не может быть пустым")
    @Size(min = 4, max = 32, message = "Заголовок объявления должен быть от 4 до 32 символов")
    @Schema(description = "Заголовок объявления")
    private String title;


    @NotNull(message = "Значение цены объявления не может быть пустым")
    @Size(min = 0, max = 10000000, message = "Цена объявления должна быть в пределах от 0 до 10000000 у.е.")
    @Schema(description = "Цена объявления")
    private Integer price;

    @NotNull(message = "Значение описания объявления не может быть пустым")
    @Size(min = 8, max = 64, message = "Описание объявления должно быть от 8 до 64 символов")
    @Schema(description = "Описание объявления")
    private String description;

    public CreateOrUpdateAd() {
    }

    public CreateOrUpdateAd(String title, Integer price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
