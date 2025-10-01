package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;

public class Register {
    @NotNull(message = "Значение логина не может быть пустым")
    @Size(min = 4, max = 32, message = "Текущий логин должен быть от 4 до 32 символов")
    @Schema(description = "Логин")
    private String username;


    @NotNull(message = "Значение текущего пароля не может быть пустым")
    @Size(min = 8, max = 16, message = "Текущий пароль должен быть от 4 до 32 символов")
    @Schema(description = "Пароль")
    private String password;


    @NotNull(message = "Значение имени не может быть пустым")
    @Size(min = 2, max = 16, message = "Имя должно быть от 2 до 16 символов")
    @Schema(description = "Имя пользователя")
    private String firstName;


    @NotNull(message = "Значение фамилии не может быть пустым")
    @Size(min = 2, max = 16, message = "Фамилия должно быть от 2 до 16 символов")
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

}
