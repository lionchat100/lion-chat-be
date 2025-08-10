package com.lion.be.feed_comment.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveRequest;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveResponse;
import com.lion.be.feed_comment.domain.dto.FeedCommentUpdateRequest;
import com.lion.be.feed_comment.domain.dto.FeedCommentUpdateResponse;
import com.lion.be.feed_comment.service.FeedCommentReadService;
import com.lion.be.feed_comment.service.FeedCommentWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedCommentController {

    private final FeedCommentReadService feedCommentReadService;
    private final FeedCommentWriteService feedCommentWriteService;

    @PostMapping("/api/feeds/{feedId}/comments")
    public ResponseEntity<FeedCommentSaveResponse> save(@PathVariable Long feedId,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @RequestBody FeedCommentSaveRequest request) {
        FeedCommentSaveResponse response = feedCommentWriteService.save(feedId, userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/feeds/{feedId}/comments")
    public ResponseEntity<Slice<FeedCommentResponse>> fetchAll(@PathVariable Long feedId, Pageable pageable,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Slice<FeedCommentResponse> response = feedCommentReadService.fetchAll(feedId, pageable, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/feeds/comments/{commentId}")
    public ResponseEntity<FeedCommentUpdateResponse> update(@PathVariable Long commentId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                            @RequestBody FeedCommentUpdateRequest request) {
        FeedCommentUpdateResponse response = feedCommentWriteService.update(commentId, userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/feeds/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId,
                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        feedCommentWriteService.delete(commentId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    //테스트용 단일 댓글 조회
    @GetMapping("/api/feeds/comments/{commentId}")
    public ResponseEntity<FeedCommentResponse> fetchById(@PathVariable Long commentId,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        FeedCommentResponse response = feedCommentReadService.fetchById(commentId, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

}
