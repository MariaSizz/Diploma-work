package com.trade.tradeApplication.controller;

import com.trade.tradeApplication.model.Comment;
import com.trade.tradeApplication.model.Comments;
import com.trade.tradeApplication.model.CreateOrUpdateComment;
import com.trade.tradeApplication.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Комментарии", description = "API для работы с комментариями")
    @RestController
    @RequestMapping
    public class CommentsController {

        private final CommentService commentService;

        public CommentsController(CommentService commentService) {
            this.commentService = commentService;
        }

        @Operation(
                summary = "Получение комментариев объявления",
                parameters = {
                        @Parameter(name = "adId", description = "ID объявления", required = true)
                },
                responses = {
                        @ApiResponse(responseCode = "200", description = "OK",
                                content = @Content(schema = @Schema(implementation = Comments.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Not found")
                }
        )
        @GetMapping("/ads/{adId}/comments")
        @CrossOrigin(origins = "http://localhost:3000")
        public ResponseEntity<Comments> getComments(@PathVariable("adId") int adId) {
            return commentService.getCommentsByAdId(adId);
        }

        @Operation(
                summary = "Добавление комментария к объявлению",
                parameters = {
                        @Parameter(name = "adId", description = "ID объявления", required = true)
                },
                requestBody = @RequestBody(
                        required = true,
                        content = @Content(schema = @Schema(implementation = CreateOrUpdateComment.class))
                ),
                responses = {
                        @ApiResponse(responseCode = "200", description = "OK",
                                content = @Content(schema = @Schema(implementation = Comment.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Not found")
                }
        )
        @PostMapping("/ads/{adId}/comments")
        @CrossOrigin(origins = "http://localhost:3000")
        public ResponseEntity<Comment> addComment(@PathVariable("adId") int adId, @Valid @org.springframework.web.bind.annotation.RequestBody CreateOrUpdateComment comment, Authentication authentication) {
            return commentService.createComment(adId, comment, authentication);
        }

        @Operation(
                summary = "Удаление комментария",
                parameters = {
                        @Parameter(name = "adId", description = "ID объявления", required = true),
                        @Parameter(name = "commentId", description = "ID комментария", required = true)
                },
                responses = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Not found")
                }
        )
        @DeleteMapping("/ads/{adId}/comments/{commentId}")
        @CrossOrigin(origins = "http://localhost:3000")
        public ResponseEntity<Void> deleteComment(@PathVariable int adId, @PathVariable int commentId, Authentication authentication) {
            return commentService.deleteComment(adId, commentId, authentication);
        }

        @Operation(
                summary = "Обновление комментария",
                parameters = {
                        @Parameter(name = "adId", description = "ID объявления", required = true),
                        @Parameter(name = "commentId", description = "ID комментария", required = true)
                },
                requestBody = @RequestBody(
                        required = true,
                        content = @Content(schema = @Schema(implementation = CreateOrUpdateComment.class))
                ),
                responses = {
                        @ApiResponse(responseCode = "200", description = "OK",
                                content = @Content(schema = @Schema(implementation = Comment.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Not found")
                }
        )
        @PatchMapping("/ads/{adId}/comments/{commentId}")
        @CrossOrigin(origins = "http://localhost:3000")
        public ResponseEntity<Comment> updateComment(@PathVariable int adId, @PathVariable int commentId,
                                                     @Valid @org.springframework.web.bind.annotation.RequestBody CreateOrUpdateComment update, Authentication authentication) {
            return commentService.updateComment(adId, commentId, update, authentication);
        }
}
