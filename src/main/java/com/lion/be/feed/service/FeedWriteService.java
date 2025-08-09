package com.lion.be.feed.service;

import com.lion.be.feed.controller.dto.FeedSaveResponse;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedWriteService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteFeed(Long currentUserId, Long feedId){
        User user = userRepository.fetchById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Feed feed = feedRepository.fetchById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

        Long feedWriterId = feed.getUser().getId();
        if(!feedWriterId.equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_UNAUTHORIZED);
        }

        feed.delete();
    }

    @Transactional
    public FeedSaveResponse writeFeed(String title, String content, Long userID){
        User user = userRepository.fetchById(userID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Feed feed = new Feed(title, content, user);
        user.addUserFeed(feed);

        Feed savedFeed = feedRepository.save(feed);
        return new FeedSaveResponse(savedFeed.getId());
    }

    @Transactional
    public void updateFeed(Long feedId, String title, String content, Long userId) {
        User user = userRepository.fetchById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Feed feed = feedRepository.fetchById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

        Long feedWriterId = feed.getUser().getId();
        if (!feedWriterId.equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_UNAUTHORIZED);
        }

        feed.update(title, content);
    }
}
