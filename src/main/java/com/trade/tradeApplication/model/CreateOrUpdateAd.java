package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateOrUpdateAd {

    @NotNull(message = "Значение заголовка объявления не может быть пустым")
    @Min(value = 4, message = "Заголовок объявления выходит за пределы минимального значения")
    @Max(value = 32, message = "Заголовок объявления выходит за пределы максимального значения")
    @Schema(description = "Заголовок объявления")
    private String title;


    @NotNull(message = "Значение цены объявления не может быть пустым")
    @Min(value = 0, message = "Цена объявления выходит за пределы минимального значения")
    @Max(value = 10000000, message = "Цена объявления выходит за пределы максимального значения")
    @Schema(description = "Цена объявления")
    private Integer price;

    @NotNull(message = "Значение описания объявления не может быть пустым")
    @Min(value = 8, message = "Описание объявления выходит за пределы минимального значения")
    @Max(value = 64, message = "Описание объявления выходит за пределы максимального значения")
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
