package com.trade.tradeApplication.controller;

import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Пользователи", description = "API для работы с пользователями")
@RestController
@RequestMapping
public class UserController {

    @Operation(
            summary = "Обновление пароля",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = NewPassword.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PostMapping("/users/set_password")
    public ResponseEntity<Void> setPassword(@org.springframework.web.bind.annotation.RequestBody NewPassword newPassword) {
        return ResponseEntity.badRequest().build();
    }

    @Operation(
            summary = "Получение информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/users/me")
    public ResponseEntity<User> getUser () {
        return ResponseEntity.ok(new User());
    }

    @Operation(
            summary = "Обновление информации об авторизованном пользователе",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUser.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = UpdateUser .class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping("/users/me")
    public ResponseEntity<UpdateUser> updateUser (@org.springframework.web.bind.annotation.RequestBody UpdateUser  updateUser ) {
        return new ResponseEntity<>(new UpdateUser(), HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Обновление аватара авторизованного пользователя",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "object", requiredProperties = {"image"},
                                    properties = {
                                            @StringToClassMapItem(key = "image", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping(value = "/users/me/image", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateUserImage(@RequestParam("image") String image) {
        return ResponseEntity.badRequest().build();
    }
}
