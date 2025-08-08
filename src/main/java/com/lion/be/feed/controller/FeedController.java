package com.lion.be.feed.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.dto.FeedUpdateRequest;
import com.lion.be.feed.controller.dto.FeedSaveResponse;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.dto.FeedWriteRequest;
import com.lion.be.feed.service.FeedReadService;
import com.lion.be.feed.service.FeedWriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedReadService feedReadService;
    private final FeedWriteService feedWriteService;

    @GetMapping("/api/feeds")
    public List<FeedResponse> showRecentFeeds(@RequestParam(value = "lastId", required = false) Long lastId) {
        if (lastId != null && lastId > 0) {
            return feedReadService.getRecentFeedsAfter(lastId);
        }

        return feedReadService.getRecentFeedsFirst();
    }

    @GetMapping("/api/feeds/hot")
    public List<FeedResponse> showHotFeeds(
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount,
            @RequestParam(value = "lastId", required = false) Long lastId) {
        if (lastLikeCount != null && lastId != null && lastLikeCount > 0 && lastId > 0) {
            return feedReadService.getHotFeedsAfter(lastLikeCount, lastId);
        }
        return feedReadService.getHotFeedsFirst();
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
}
