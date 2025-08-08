package com.lion.be.feed.service;

import com.lion.be.feed.domain.dto.FeedDto;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.dto.FeedWriterDto;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.global.exception.FeedNotFoundException;
import com.lion.be.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedReadService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    public Feed fetchById(Long id) {
        return feedRepository.findById(id)
                .orElseThrow(FeedNotFoundException::new);
    }

    public List<FeedResponse> getRecentFeedsFirst() {
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchRecentFeedsFirst(pageable)
                .stream()
                .map(feed -> new FeedResponse(
                        new FeedDto(
                                feed.getId(),
                                feed.getTitle(),
                                feed.getContent(),
                                feed.getCreatedAt(),
                                0L,
                                false,
                                0L),
                        new FeedWriterDto(
                                feed.getUser().getName(),
                                feed.getUser().getId(),
                                feed.getUser().getImageUrl()
                        )
                ))
                .toList();
    }

    public List<FeedResponse> getRecentFeedsAfter(Long lastId){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchRecentFeedsAfter(lastId, pageable)
                .stream()
                .map(feed -> new FeedResponse(
                        new FeedDto(
                                feed.getId(),
                                feed.getTitle(),
                                feed.getContent(),
                                feed.getCreatedAt(),
                                0L,
                                false,
                                0L),
                        new FeedWriterDto(
                                feed.getUser().getName(),
                                feed.getUser().getId(),
                                feed.getUser().getImageUrl()
                        )
                ))
                .toList();
    }

    public List<FeedResponse> getHotFeedsFirst(){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchHotFeedsFirst(pageable)
                .stream()
                .map(feed -> new FeedResponse(
                        new FeedDto(
                                feed.getId(),
                                feed.getTitle(),
                                feed.getContent(),
                                feed.getCreatedAt(),
                                0L,
                                false,
                                0L),
                        new FeedWriterDto(
                                feed.getUser().getName(),
                                feed.getUser().getId(),
                                feed.getUser().getImageUrl()
                        )
                ))
                .toList();
    }

    public List<FeedResponse> getHotFeedsAfter(Long lastLikeCount, Long lastId){
        Pageable pageable = PageRequest.of(0, 30);
        return feedRepository.fetchHotFeedsAfter(lastLikeCount, lastId, pageable)
                .stream()
                .map(feed -> new FeedResponse(
                        new FeedDto(
                                feed.getId(),
                                feed.getTitle(),
                                feed.getContent(),
                                feed.getCreatedAt(),
                                0L,
                                false,
                                0L),
                        new FeedWriterDto(
                                feed.getUser().getName(),
                                feed.getUser().getId(),
                                feed.getUser().getImageUrl()
                        )
                ))
                .toList();
    }

}
