package com.trade.tradeApplication.service.impl;

import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.entity.CommentEntity;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.mapper.CommentMapper;
import com.trade.tradeApplication.model.*;
import com.trade.tradeApplication.repository.AdRepository;
import com.trade.tradeApplication.repository.CommentRepository;
import com.trade.tradeApplication.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository,
                              AdRepository adRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.adRepository = adRepository;
        this.commentMapper = commentMapper;
    }

    public ResponseEntity<Comments> getCommentsByAdId(int adId) {
        if (!adRepository.existsById(adId)) {
            throw new EntityNotFoundException("Ad not found");
        }

        List<CommentEntity> commentsEntity = commentRepository.findByAdPk(adId);

        List<Comment> comments = commentsEntity.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        Comments commentsToSend = new Comments();
        commentsToSend.setResults(comments);
        commentsToSend.setCount(comments.size());
        return ResponseEntity.ok(commentsToSend);
    }

    public ResponseEntity<Comment> createComment(int adId, CreateOrUpdateComment dto, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found with id: " + adId));

        CommentEntity commentEntity = commentMapper.toEntityFromCreateOrUpdateComment(dto);
        commentEntity.setAuthor(user);
        commentEntity.setAd(ad);
        commentEntity.setCreatedAt(System.currentTimeMillis());

        CommentEntity saved = commentRepository.save(commentEntity);
        return ResponseEntity.ok(commentMapper.toDto(saved));
    }

    public ResponseEntity<Comment> updateComment(int adId, int commentId, CreateOrUpdateComment dto, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity adEntity = adRepository.findById(adId).orElse(null);
        if (adEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        // Проверка прав: админ или автор комментария
        if (user.getRole() != Role.ADMIN && !comment.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        comment.setText(dto.getText());
        comment.setCreatedAt(System.currentTimeMillis());
        commentRepository.save(comment);

        return ResponseEntity.ok(commentMapper.toDto(comment));
    }

    public ResponseEntity<Void> deleteComment(int adId, int commentId, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        AdEntity adEntity = adRepository.findById(adId).orElse(null);
        if (adEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (user.getRole() != Role.ADMIN && !comment.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        commentRepository.deleteById(commentId);
        return ResponseEntity.ok().build();
    }

    private UserEntity getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser ();
        }
        return null;
    }
}
