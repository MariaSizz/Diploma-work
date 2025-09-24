package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class Login implements Serializable {
    @NotNull(message = "Значение не может быть пустым")
    @Min(value = 4, message = "Логин выходит за пределы минимального значения")
    @Max(value = 32, message = "Логин выходит за пределы максимального значения")
    @Schema(description = "Логин")
    private String username;

    @NotNull(message = "Значение не может быть пустым")
    @Min(value = 8, message = "Пароль выходит за пределы минимального значения")
    @Max(value = 16, message = "Пароль выходит за пределы максимального значения")
    @Schema(description = "Пароль")
    private String password;

public Role role;
    public Login() {
    }

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public enum Role{
        USER,
        ADMIN
    }
}
