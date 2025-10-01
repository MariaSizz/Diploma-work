package com.trade.tradeApplication.service;

import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.model.Ad;
import com.trade.tradeApplication.model.Ads;
import com.trade.tradeApplication.model.CreateOrUpdateAd;
import com.trade.tradeApplication.model.ExtendedAd;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AdService {

     Ads getAllAds();

     ResponseEntity<ExtendedAd> getAdById(int id);

    ResponseEntity<AdEntity> createAd(CreateOrUpdateAd dto, String image, Authentication auth);

    ResponseEntity<Ad> updateAd(int id, CreateOrUpdateAd update, Authentication auth);

    ResponseEntity<Void> deleteAd(int id, Authentication auth);

    ResponseEntity<Ads> getMyAds(Authentication auth);
    ResponseEntity<Void> updateAdImage(int id, String image, Authentication auth);
}
