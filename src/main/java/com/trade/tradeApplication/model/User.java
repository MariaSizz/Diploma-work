package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class User {
    @Schema(description = "id пользователя")
    private Integer id;

    @Schema(description = "Логин пользователя")
    private  String email;

    @Schema(description = "Имя пользователя")
    private  String firstName;

    @Schema(description = "Фамилия пользователя")
    private  String lastName;

    @Schema(description = "Телефон пользователя")
    private  String phone;

    @Schema(description = "Роль пользователя")
    private  Role role;

    @Schema(description = "Ссылка на аватар пользователя")
    private  String image;



    public User() {
    }

    public User(Integer id, String email, String firstName, String lastName, String phone, Role role, String image) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public enum Role{USER, ADMIN}


}
