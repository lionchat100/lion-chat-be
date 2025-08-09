package com.lion.be.feed.repository;

import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    //첫 전체 조회
    @Query("""
            select
            new com.lion.be.feed.domain.dto.FeedResponse(f.id, f.title, f.content, f.createdAt, 0L, false, 0L, u.name, u.id, u.imageUrl)
            from Feed f
            join f.user u
            where f.isDeleted = false""")
    Slice<FeedResponse> fetchRecentFeedsFirst(Pageable pageable);

    @Query("select f from Feed f join fetch f.user where f.id = :id and f.isDeleted = false")
    Optional<Feed> fetchById(Long id);

    //이후 전체 조회
    @Query("""
    select new com.lion.be.feed.domain.dto.FeedResponse(f.id, f.title, f.content, f.createdAt, 0L, false, 0L, u.name, u.id, u.imageUrl)
    from Feed f
    join f.user u
    where f.isDeleted = false and f.id < :lastId
    """)
    Slice<FeedResponse> fetchRecentFeedsAfter(Long lastId, Pageable pageable);

    //첫 좋아요 순 조회(커서 페이지네이션(1차적으로 like_count, 2차적으로는 게시글의 pk), 페이징, 기간 별 or 전체 기간)
    @Query("""
    select new com.lion.be.feed.domain.dto.FeedResponse(f.id, f.title, f.content, f.createdAt, 0L, false, 0L, u.name, u.id, u.imageUrl)
    from Feed f
    join f.user u
    where f.isDeleted = false
    """)
    //추가할 쿼리: join like_count, order by like_count desc, f.id desc
    Slice<FeedResponse> fetchHotFeedsFirst(Pageable pageable);

    //이후 좋아요 순 조회
    @Query("""
    select new com.lion.be.feed.domain.dto.FeedResponse(f.id, f.title, f.content, f.createdAt, 0L, false, 0L, u.name, u.id, u.imageUrl)
    from Feed f
    join f.user u
    where f.isDeleted = false
    """)
    //추가할 쿼리: join like_count, like_count < :lastLikeCount or (like_count = :lastLikeCount and id < lastId) ,order by like_count desc, f.id desc
    Slice<FeedResponse> fetchHotFeedsAfter(Long lastLikeCount, Long lastId, Pageable pageable);

    @Query("select f from Feed f where f.id = :id and f.isDeleted = false")
    Optional<Feed> findFeed(Long id);

    //생각할 것: 검색이 되는가? 검색이 된다면 어디까지 될 것인가?
}
