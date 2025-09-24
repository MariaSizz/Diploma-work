package com.trade.tradeApplication.repository;

import com.trade.tradeApplication.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findAllByAuthorId(Integer authorId);
}

