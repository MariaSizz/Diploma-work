package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class Login implements Serializable {
    @NotNull(message = "Значение не может быть пустым")
    @Size(min = 4, max = 32, message = "Логин должен быть от 4 до 32 символов")
    @Schema(description = "Логин")
    private String username;

    @NotNull(message = "Значение не может быть пустым")
    @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
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
}
