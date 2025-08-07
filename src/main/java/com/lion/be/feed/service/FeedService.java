package com.lion.be.feed.service;

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
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    //Todo: 커스텀 예외 던지기
    @Transactional
    public void writeFeed(String title, String content, Long userID){
        User user = userRepository.fetchById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userID));

        Feed feed = new Feed(title, content, user);
        user.addUserFeed(feed);

        feedRepository.save(feed);
    }

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

    public List<FeedListResponse> getRecentFeedsFirst(){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchRecentFeedsFirst(pageable)
                .stream()
                .map(feed -> new FeedListResponse(
                        feed.getId(),
                        feed.getTitle(),
                        feed.getContent(),
                        feed.getUser().getName(),
                        feed.getUser().getId(),
                        feed.getUser().getImageUrl(),
                        0L, // Todo: 구현 시 교체
                        false, // Todo: 구현 시 교체
                        0L, // Todo: 구현 시 교체
                        feed.getCreatedAt()
                ))
                .toList();
    }

    public List<FeedListResponse> getRecentFeedsAfter(Long lastId){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchRecentFeedsAfter(lastId, pageable)
                .stream()
                .map(feed -> new FeedListResponse(
                        feed.getId(),
                        feed.getTitle(),
                        feed.getContent(),
                        feed.getUser().getName(),
                        feed.getUser().getId(),
                        feed.getUser().getImageUrl(),
                        0L, // Todo: 구현 시 교체
                        false, // Todo: 구현 시 교체
                        0L, // Todo: 구현 시 교체
                        feed.getCreatedAt()
                ))
                .toList();
    }

    public List<FeedListResponse> getHotFeedsFirst(){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchHotFeedsFirst(pageable)
                .stream()
                .map(feed -> new FeedListResponse(
                        feed.getId(),
                        feed.getTitle(),
                        feed.getContent(),
                        feed.getUser().getName(),
                        feed.getUser().getId(),
                        feed.getUser().getImageUrl(),
                        0L, // Todo: 구현 시 교체
                        false, // Todo: 구현 시 교체
                        0L, // Todo: 구현 시 교체
                        feed.getCreatedAt()
                ))
                .toList();
    }

    public List<FeedListResponse> getHotFeedsAfter(Long lastLikeCount, Long lastId){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchHotFeedsAfter(lastLikeCount, lastId, pageable)
                .stream()
                .map(feed -> new FeedListResponse(
                        feed.getId(),
                        feed.getTitle(),
                        feed.getContent(),
                        feed.getUser().getName(),
                        feed.getUser().getId(),
                        feed.getUser().getImageUrl(),
                        0L, // Todo:. 구현 시 교체
                        false, // Todo: 구현 시 교체
                        0L, // Todo: 구현 시 교체
                        feed.getCreatedAt()
                ))
                .toList();
    }




}
