package com.lion.be.feed.service;

import com.lion.be.feed.domain.dto.FeedListResponse;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedReadService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

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
