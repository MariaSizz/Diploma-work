package com.trade.tradeApplication.controller;

import com.trade.tradeApplication.model.Login;
import com.trade.tradeApplication.model.Register;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Регистрация и Авторизация", description = "API для регистрации и авторизации пользователей")
public class AuthorizationController {

    @Operation(
            summary = "Регистрация пользователя",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = Register.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@org.springframework.web.bind.annotation.RequestBody Register register) {
        return ResponseEntity.badRequest().build();
    }

    @Operation(
            summary = "Авторизация пользователя",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = Login.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login(@org.springframework.web.bind.annotation.RequestBody Login login) {
        return ResponseEntity.badRequest().build();
    }
}
