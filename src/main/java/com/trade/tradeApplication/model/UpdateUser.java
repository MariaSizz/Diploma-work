package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UpdateUser {

    @NotNull(message = "Значение имени не может быть пустым")
    @Min(value = 3, message = "Имя выходит за пределы минимального значения")
    @Max(value = 10, message = "Имя выходит за пределы максимального значения")
    @Schema(description = "Имя пользователя")
    private String firstName;

    @NotNull(message = "Значение фамилии не может быть пустым")
    @Min(value = 3, message = "Фамилия выходит за пределы минимального значения")
    @Max(value = 10, message = "Фамилия выходит за пределы максимального значения")
    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @NotNull(message = "Значение номера телефона не может быть пустым")
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    @Schema(description = "Телефон пользователя")
    private String phone;

    public UpdateUser() {
    }

    public UpdateUser(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
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
}
