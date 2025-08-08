package com.lion.be.feed.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed.controller.dto.FeedSaveResponse;
import com.lion.be.feed.domain.dto.FeedListResponse;
import com.lion.be.feed.domain.dto.FeedWriteRequest;
import com.lion.be.feed.service.FeedReadService;
import com.lion.be.feed.service.FeedWriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedReadService feedReadService;
    private final FeedWriteService feedWriteService;

    @GetMapping("/api/feeds")
    public List<FeedListResponse> showRecentFeeds(@RequestParam(value = "lastId", required = false) Long lastId) {
        if (lastId != null && lastId > 0) {
            return feedReadService.getRecentFeedsAfter(lastId);
        }

        return feedReadService.getRecentFeedsFirst();
    }

    @GetMapping("/api/feeds/hot")
    public List<FeedListResponse> showHotFeeds(
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

}
