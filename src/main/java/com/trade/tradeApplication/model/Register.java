package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Register {
    @NotNull(message = "Значение логина не может быть пустым")
    @Min(value = 4, message = "Текущий логин выходит за пределы минимального значения")
    @Max(value = 32, message = "Текущий логин выходит за пределы максимального значения")
    @Schema(description = "Логин")
    private String username;


    @NotNull(message = "Значение текущего пароля не может быть пустым")
    @Min(value = 8, message = "Текущий пароль выходит за пределы минимального значения")
    @Max(value = 16, message = "Текущий пароль выходит за пределы максимального значения")
    @Schema(description = "Пароль")
    private String password;


    @NotNull(message = "Значение имени не может быть пустым")
    @Min(value = 2, message = "Имя выходит за пределы минимального значения")
    @Max(value = 16, message = "Имя выходит за пределы максимального значения")
    @Schema(description = "Имя пользователя")
    private String firstName;


    @NotNull(message = "Значение фамилии не может быть пустым")
    @Min(value = 2, message = "Фамилия выходит за пределы минимального значения")
    @Max(value = 16, message = "Фамилия выходит за пределы максимального значения")
    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @NotNull(message = "Значение номера телефона не может быть пустым")
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    @Schema(description = "Телефон пользователя")
    private String phone;

    @NotNull(message = "Значение роли не может быть пустым")
    @Schema(description = "Роль пользователя")
    private Role role;

    public Register() {
    }

    public Register(String username, String password, String firstName, String lastName, String phone, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public enum Role{USER, ADMIN}

}
