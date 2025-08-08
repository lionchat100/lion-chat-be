package com.lion.be.feed.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.feed.domain.dto.FeedListResponse;
import com.lion.be.feed.domain.dto.FeedWriteRequest;
import com.lion.be.feed.service.FeedReadService;
import com.lion.be.feed.service.FeedWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {
    private final FeedReadService feedReadService;
    private final FeedWriteService feedWriteService;

    @GetMapping("/api/feeds")
    public List<FeedListResponse> showRecentFeeds(@RequestParam(value = "lastId", required = false) Long lastId) {
        if(lastId != null && lastId > 0)
            return feedReadService.getRecentFeedsAfter(lastId);

        return feedReadService.getRecentFeedsFirst();
    }

    @GetMapping("/api/feeds/hot")
    public List<FeedListResponse> showHotFeeds(@RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount,
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
    public void writeFeed(@RequestBody FeedWriteRequest feedWriteRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        feedWriteService.writeFeed(feedWriteRequest.getTitle(), feedWriteRequest.getContent(), userId);
    }

    @PutMapping("/api/feeds/{feedId}")
    public void modifyFeed(@PathVariable("feedId") Long feedId,
                             @RequestBody FeedWriteRequest feedWriteRequest,
                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        feedWriteService.updateFeed(feedId, feedWriteRequest.getTitle(), feedWriteRequest.getContent(), userId);
    }
}
