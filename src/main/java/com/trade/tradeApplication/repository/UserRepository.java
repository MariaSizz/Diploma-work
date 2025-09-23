package com.trade.tradeApplication.repository;

import com.trade.tradeApplication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

}
