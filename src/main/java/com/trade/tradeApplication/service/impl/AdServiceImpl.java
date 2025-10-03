package com.trade.tradeApplication.service.impl;

import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.mapper.AdMapper;
import com.trade.tradeApplication.model.*;
import com.trade.tradeApplication.repository.AdRepository;
import com.trade.tradeApplication.service.AdService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;

    public AdServiceImpl(AdRepository adRepository, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
    }

    public Ads getAllAds() {
        List<AdEntity> all = adRepository.findAll();
        List<Ad> adsList = new ArrayList<>();

        Ads ads = new Ads();
        ads.setCount(all.size());

        for (AdEntity adEntity : all) {
            adsList.add(adMapper.toDto(adEntity));
        }
        ads.setResults(adsList);
        return ads;
    }

    public ResponseEntity<ExtendedAd> getAdById(int id) {
        AdEntity ad = adRepository.findById(id).orElse(null);
        if (ad == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(adMapper.toExtendedAd(ad));
    }


    public ResponseEntity<AdEntity> createAd(CreateOrUpdateAd dto, MultipartFile imageFile, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        AdEntity adEntity = adMapper.toAdEntity(dto);
        adEntity.setAuthor(user);
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/images/ads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(filename);
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                adEntity.setImage("/images/ads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        adRepository.save(adEntity);
        return new ResponseEntity<>(adEntity, HttpStatus.CREATED);
    }


    public ResponseEntity<Ad> updateAd(int id, CreateOrUpdateAd update, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity adEntity = adRepository.findById(id).orElse(null);
        if (adEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        if (user.getRole() != Role.ADMIN && !adEntity.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        adEntity.setTitle(update.getTitle());
        adEntity.setPrice(update.getPrice());
        adEntity.setDescription(update.getDescription());

        adRepository.save(adEntity);
        return ResponseEntity.ok(adMapper.toDto(adEntity));
    }

    public ResponseEntity<Void> deleteAd(int id, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity adEntity = adRepository.findById(id).orElse(null);
        if (adEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (user.getRole() != Role.ADMIN && !adEntity.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        adRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<Ads> getMyAds(Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<AdEntity> allByAuthorId = adRepository.findAllByAuthorId(user.getId());
        List<Ad> adsList = allByAuthorId.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());

        Ads ads = new Ads();
        ads.setCount(adsList.size());
        ads.setResults(adsList);

        return new ResponseEntity<>(ads, HttpStatus.OK);
    }


    public ResponseEntity<Void> updateAdImage(int id, MultipartFile imageFile, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        AdEntity adEntity = adRepository.findById(id).orElse(null);
        if (adEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (user.getRole() != Role.ADMIN && !adEntity.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (imageFile == null || imageFile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

            Path uploadPath = Paths.get("uploads/images/ads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            adEntity.setImage("/images/ads/" + filename);
            adRepository.save(adEntity);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Resource> getAdImage(String filename) {
        try {
            Path filePath = Paths.get("uploads/images/ads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private UserEntity getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser ();
        }
        return null;
    }
}

