package com.lion.be.feed.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed.domain.dto.FeedListResponse;
import com.lion.be.feed.domain.dto.FeedWriteRequest;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/api/feeds")
    public List<FeedListResponse> showRecentFeeds(@RequestParam(value = "lastId", required = false) Long lastId) {
        if(lastId != null && lastId > 0)
            return feedService.getRecentFeedsAfter(lastId);

        return feedService.getRecentFeedsFirst();
    }

    @GetMapping("/api/feeds/hot")
    public List<FeedListResponse> showHotFeeds(@RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount,
                                                              @RequestParam(value = "lastId", required = false) Long lastId) {
        if (lastLikeCount != null && lastId != null && lastLikeCount > 0 && lastId > 0) {
            return feedService.getHotFeedsAfter(lastLikeCount, lastId);
        }
        return feedService.getHotFeedsFirst();
    }

    @DeleteMapping("/api/feeds/{feedId}")
    public void deleteFeed(@PathVariable("feedId") Long feedId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal.getId();
        feedService.deleteFeed(currentUserId, feedId);
    }

    @PostMapping("/api/feeds")
    public void writeFeed(@RequestBody FeedWriteRequest feedWriteRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        feedService.writeFeed(feedWriteRequest.getTitle(), feedWriteRequest.getContent(), userId);
    }
}
