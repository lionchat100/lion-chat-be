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
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedReadService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_PAGE_SIZE = 30;

    public Feed fetchById(Long id) {
        return feedRepository.findFeed(id)
                .orElseThrow(FeedNotFoundException::new);
    }

    public Slice<FeedResponse> getRecentFeedsFirst(Integer size) {
        return feedRepository.fetchRecentFeedsFirst(getRecentPageable(size));
    }

    public Slice<FeedResponse> getRecentFeedsAfter(Long lastId, Integer size){
        return feedRepository.fetchRecentFeedsAfter(lastId, getRecentPageable(size));
    }

    public Slice<FeedResponse> getHotFeedsFirst(Integer size){
        return feedRepository.fetchHotFeedsFirst(getHotPageable(size));
    }

    public Slice<FeedResponse> getHotFeedsAfter(Long lastLikeCount, Long lastId, Integer size){
        return feedRepository.fetchHotFeedsAfter(lastLikeCount, lastId, getHotPageable(size));
    }


    private Pageable getRecentPageable(Integer size) {
        return PageRequest.of(0, size != null && size <= DEFAULT_PAGE_SIZE ? size : DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
    }

    private Pageable getHotPageable(Integer size) {
        //Todo: likeCount를 우선 정렬로 추가
        return PageRequest.of(0, size!= null && size <= DEFAULT_PAGE_SIZE ? size : DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
    }
}
