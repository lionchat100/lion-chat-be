package com.lion.be.feed_comment.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed_comment.service.FeedCommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedCommentLikeController {

    private final FeedCommentLikeService feedCommentLikeService;

    @PostMapping("/api/feeds/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        feedCommentLikeService.likeComment(commentId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/feeds/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        feedCommentLikeService.unlikeComment(commentId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

}