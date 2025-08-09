package com.lion.be.feed.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed.domain.dto.*;
import com.lion.be.feed.controller.dto.FeedSaveResponse;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.service.FeedReadService;
import com.lion.be.feed.service.FeedWriteService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedReadService feedReadService;
    private final FeedWriteService feedWriteService;

    @GetMapping("/api/feeds")
    public ResponseEntity<Slice<FeedResponse>> showRecentFeeds(
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false)Integer size) {
        if (lastId != null && lastId > 0) {
            return ResponseEntity.ok(feedReadService.getRecentFeedsAfter(lastId, size));
        }

        return ResponseEntity.ok(feedReadService.getRecentFeedsFirst(size));
    }

    @GetMapping("/api/feeds/hot")
    public ResponseEntity<Slice<FeedResponse>> showHotFeeds(
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false)Integer size) {
        if (lastLikeCount != null && lastId != null && lastLikeCount > 0 && lastId > 0) {
            return ResponseEntity.ok(feedReadService.getHotFeedsAfter(lastLikeCount, lastId, size));
        }
        return ResponseEntity.ok(feedReadService.getHotFeedsFirst(size));
    }

    @DeleteMapping("/api/feeds/{feedId}")
    public void deleteFeed(@PathVariable("feedId") Long feedId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal.getId();
        feedWriteService.deleteFeed(currentUserId, feedId);
    }

    @PostMapping("/api/feeds")
    public ResponseEntity<FeedSaveResponse> writeFeed(@RequestBody FeedWriteRequest feedWriteRequest,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        FeedSaveResponse response = feedWriteService.writeFeed(feedWriteRequest.getTitle(),
                feedWriteRequest.getContent(), userId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/api/feeds/{feedId}")
    public void modifyFeed(@PathVariable("feedId") Long feedId,
                             @RequestBody FeedUpdateRequest feedUpdateRequest,
                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        feedWriteService.updateFeed(feedId, feedUpdateRequest.getTitle(), feedUpdateRequest.getContent(), userId);
    }

    @GetMapping("/api/feeds/{feedId}")
    public ResponseEntity<FeedDto> getFeedById(@PathVariable("feedId") Long feedId) {
        Feed feedResponse = feedReadService.fetchById(feedId);
        FeedDto feedDto = new FeedDto(
                feedResponse.getId(),
                feedResponse.getTitle(),
                feedResponse.getContent(),
                feedResponse.getCreatedAt(),
                0L, // Placeholder for like count
                false, // Placeholder for isLiked
                0L // Placeholder for comment count
        );

        return ResponseEntity.ok(feedDto);
    }
}
