package com.trade.tradeApplication.controller;

import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.model.User;
import com.trade.tradeApplication.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Пользователи", description = "API для работы с пользователями")
@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
    public ResponseEntity<Void> setPassword(@Valid @org.springframework.web.bind.annotation.RequestBody NewPassword password, Authentication authentication) {
        return userService.setPassword(password, authentication);
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
    public ResponseEntity<User> getUser (Authentication authentication) {
        return userService.getCurrentUser(authentication);
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
    public ResponseEntity<UpdateUser> updateUser (@Valid @org.springframework.web.bind.annotation.RequestBody UpdateUser  updateUser, Authentication authentication) {
        return userService.updateCurrentUser(updateUser, authentication);
    }

    @Operation(
            summary = "Обновление аватара авторизованного пользователя",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "object", requiredProperties = {"image"}
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping(value = "/users/me/image", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateUserImage(@RequestParam("image") String image, Authentication authentication) {
        return userService.updateUserImage(image, authentication);
    }
}
