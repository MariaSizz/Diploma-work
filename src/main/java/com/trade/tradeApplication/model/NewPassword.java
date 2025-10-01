package com.trade.tradeApplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewPassword {
    @NotNull(message = "Значение текущего пароля не может быть пустым")
    @Size(min = 8, max = 16, message = "Текущий пароль должен быть от 8 до 16 символов")
    @Schema(description = "Текущий пароль")
    private String currentPassword;

    @NotNull(message = "Значение нового пароля не может быть пустым")
    @Size(min = 8, max = 16, message = "Новый пароль должен быть от 8 до 16 символов")
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
