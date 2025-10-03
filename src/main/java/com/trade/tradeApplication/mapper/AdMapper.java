package com.trade.tradeApplication.mapper;

import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.model.Ad;
import com.trade.tradeApplication.model.CreateOrUpdateAd;
import com.trade.tradeApplication.model.ExtendedAd;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface AdMapper {
    @Mapping(source = "author.id", target = "author")
    Ad toDto(AdEntity ad);
    @Mapping(source = "author", target = "author.id")
    AdEntity toEntity(Ad dto);
    ExtendedAd toExtendedAd(AdEntity ad);

    AdEntity toAdEntity(CreateOrUpdateAd createOrUpdateAd);
}
