package com.trade.tradeApplication.mapper;

import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.User;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDto(UserEntity userEntity);
    UserEntity toEntity(User dto);
}
