package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class NewPassword {
    @NotNull(message = "Значение текущего пароля не может быть пустым")
    @Min(value = 8, message = "Текущий пароль выходит за пределы минимального значения")
    @Max(value = 16, message = "Текущий пароль выходит за пределы максимального значения")
    @Schema(description = "Текущий пароль")
    private String currentPassword;

    @NotNull(message = "Значение нового пароля не может быть пустым")
    @Min(value = 8, message = "Новый пароль выходит за пределы минимального значения")
    @Max(value = 16, message = "Новый пароль выходит за пределы максимального значения")
    @Schema(description = "Новый пароль")
    private String newPassword;


    public NewPassword() {
    }

    public NewPassword(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
