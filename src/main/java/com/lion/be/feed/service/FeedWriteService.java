package com.lion.be.feed.service;

import com.lion.be.feed.controller.dto.FeedSaveResponse;
import com.lion.be.feed.domain.dto.FeedListResponse;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedWriteService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    //Todo: 커스텀 예외 던지기
    @Transactional
    public void deleteFeed(Long currentUserId, Long feedId){
        User user = userRepository.fetchById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + currentUserId));

        Feed feed = feedRepository.fetchById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));

        Long feedWriterId = feed.getUser().getId();
        if(!feedWriterId.equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this feed.");
        }

        feed.delete();
    }

    //Todo: 커스텀 예외 던지기
    @Transactional
    public FeedSaveResponse writeFeed(String title, String content, Long userID){
        User user = userRepository.fetchById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userID));

        Feed feed = new Feed(title, content, user);
        user.addUserFeed(feed);

        Feed savedFeed = feedRepository.save(feed);
        return new FeedSaveResponse(savedFeed.getId());
    }
}
