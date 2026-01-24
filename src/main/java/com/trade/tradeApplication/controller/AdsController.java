package com.trade.tradeApplication.controller;

import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.model.Ad;
import com.trade.tradeApplication.model.Ads;
import com.trade.tradeApplication.model.CreateOrUpdateAd;
import com.trade.tradeApplication.model.ExtendedAd;
import com.trade.tradeApplication.service.AdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Объявления", description = "API для работы с объявлениями")
@RestController
@RequestMapping("/ads")
public class AdsController {

    private final AdService adService;

    public AdsController(AdService adService) {
        this.adService = adService;
    }

    @Operation(
            summary = "Получение всех объявлений",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Ads.class)))
            }
    )
    @GetMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Ads> getAllAds() {
        return new ResponseEntity<>(adService.getAllAds(), HttpStatus.OK);
    }

    @Operation(
            summary = "Добавление объявления",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "object", requiredProperties = {"image", "properties"}

                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(schema = @Schema(implementation = Ad.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<AdEntity> addAd(@RequestPart("properties") CreateOrUpdateAd properties, @RequestPart("image") MultipartFile image, Authentication authentication) {
        return adService.createAd(properties, image, authentication);
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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable int id) {
       return adService.getAdById(id);
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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> removeAd(@PathVariable int id, Authentication auth) {
        return adService.deleteAd(id, auth);
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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Ad> updateAds(@PathVariable int id, @org.springframework.web.bind.annotation.RequestBody CreateOrUpdateAd update, Authentication authentication) {
        return adService.updateAd(id, update, authentication);
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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        return adService.getMyAds(authentication);
    }

    @Operation(
            summary = "Обновление картинки объявления",
            parameters = {
                    @Parameter(name = "id", description = "ID объявления", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "object", requiredProperties = {"image"}

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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> updateImage(@PathVariable int id, @RequestParam("image") MultipartFile image, Authentication authentication) {
        return adService.updateAdImage(id, image, authentication);
    }

    @GetMapping(value = "/images/ads/{filename}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Resource> getAdImage(@PathVariable String filename) {
        return adService.getAdImage(filename);
    }
}