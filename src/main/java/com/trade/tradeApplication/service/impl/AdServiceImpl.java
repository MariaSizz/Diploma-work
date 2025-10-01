package com.trade.tradeApplication.service.impl;

import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.mapper.AdMapper;
import com.trade.tradeApplication.model.*;
import com.trade.tradeApplication.repository.AdRepository;
import com.trade.tradeApplication.service.AdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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


    public ResponseEntity<AdEntity> createAd(CreateOrUpdateAd dto, String image, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity adEntity = adMapper.toAdEntity(dto);
        adEntity.setAuthor(user);
        adEntity.setImage(image);
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


    public ResponseEntity<Void> updateAdImage(int id, String image, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity adEntity = adRepository.findById(id).orElse(null);
        if (adEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (user.getRole() != Role.ADMIN && !adEntity.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        adEntity.setImage(image);
        adRepository.save(adEntity);

        return new ResponseEntity<>(HttpStatus.OK);
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

