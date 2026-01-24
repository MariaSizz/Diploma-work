package com.trade.tradeApplication.service;

import com.trade.tradeApplication.model.Comment;
import com.trade.tradeApplication.model.Comments;
import com.trade.tradeApplication.model.CreateOrUpdateComment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface CommentService {

    ResponseEntity<Comments> getCommentsByAdId(int adId);

    ResponseEntity<Comment> createComment(int adId, CreateOrUpdateComment dto, Authentication auth);

    ResponseEntity<Comment> updateComment(int adId, int commentId, CreateOrUpdateComment dto, Authentication auth);

    ResponseEntity<Void> deleteComment(int adId, int commentId, Authentication auth);
}
