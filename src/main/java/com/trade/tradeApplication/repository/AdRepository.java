package com.trade.tradeApplication.repository;

import com.trade.tradeApplication.entity.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AdRepository extends JpaRepository<AdEntity, Integer> {
    List<AdEntity> findAllByAuthorId(Integer authorId);
}

