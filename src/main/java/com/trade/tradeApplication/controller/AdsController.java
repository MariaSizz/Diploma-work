package com.trade.tradeApplication.controller;

import com.trade.tradeApplication.model.Ad;
import com.trade.tradeApplication.model.Ads;
import com.trade.tradeApplication.model.CreateOrUpdateAd;
import com.trade.tradeApplication.model.ExtendedAd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Объявления", description = "API для работы с объявлениями")
@RestController
@RequestMapping("/ads")
public class AdsController {

    @Operation(
            summary = "Получение всех объявлений",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Ads.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return new ResponseEntity<>(new Ads(), HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Добавление объявления",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "object", requiredProperties = {"image", "properties"},
                                    properties = {
                                            @StringToClassMapItem(key = "properties", value = CreateOrUpdateAd.class),
                                            @StringToClassMapItem(key = "image", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(schema = @Schema(implementation = Ad.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Ad> addAd(
            @RequestPart("properties") CreateOrUpdateAd properties,
            @RequestPart("image") String image) {
        return new ResponseEntity<>(new Ad(), HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Получение информации об объявлении",
            parameters = {
                    @Parameter(name = "id", description = "ID объявления", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ExtendedAd.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable int id) {
        ExtendedAd ad =new ExtendedAd();
        return new ResponseEntity<>(ad, HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Удаление объявления",
            parameters = {
                    @Parameter(name = "id", description = "ID объявления", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable int id) {
        return ResponseEntity.badRequest().build();
    }

    @Operation(
            summary = "Обновление информации об объявлении",
            parameters = {
                    @Parameter(name = "id", description = "ID объявления", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateOrUpdateAd.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Ad.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAds(@PathVariable int id, @org.springframework.web.bind.annotation.RequestBody CreateOrUpdateAd update) {
        Ad updatedAd =new Ad();
        return new ResponseEntity<>(updatedAd, HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Ads.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe() {
        return new ResponseEntity<>(new Ads(), HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Обновление картинки объявления",
            parameters = {
                    @Parameter(name = "id", description = "ID объявления", required = true)
            },
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
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/octet-stream",
                                    array = @ArraySchema(schema = @Schema(type = "string", format = "byte")))),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<String> updateImage(@PathVariable int id, @RequestParam("image") String image) {
        return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
    }
}