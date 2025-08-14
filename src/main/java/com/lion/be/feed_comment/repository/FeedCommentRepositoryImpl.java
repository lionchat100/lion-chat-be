package com.lion.be.feed_comment.repository;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveResponse;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import com.lion.be.feed_comment.domain.entity.QFeedComment;
import com.lion.be.feed_comment.repository.persistence.jpa.FeedCommentJpaRepository;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class FeedCommentRepositoryImpl implements FeedCommentRepository {

    private final FeedCommentJpaRepository feedCommentJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<FeedCommentResponse> fetchAllByFeedId(Long feedId, Pageable pageable) {
        return feedCommentJpaRepository.fetchAllByFeedId(feedId, pageable);
    }

    @Override
    public FeedCommentSaveResponse save(FeedComment feedComment) {
        FeedComment savedFeedComment = feedCommentJpaRepository.save(feedComment);
        return new FeedCommentSaveResponse(savedFeedComment.getId());
    }

    @Override
    public void deleteById(Long id) {
        feedCommentJpaRepository.softDeleteById(id);
    }

    @Override
    public void updateLikeCount(Long commentId, long likeCount) {
        feedCommentJpaRepository.updateLikeCount(commentId, likeCount);
    }

    @Override
    public FeedCommentResponse findCommentById(Long commentId) {
        return feedCommentJpaRepository.findCommentById(commentId);
    }

    @Override
    public void softDeleteByFeedId(Long feedId) {
        feedCommentJpaRepository.softDeleteByFeedId(feedId);
    }

    @Override
    public FeedComment fetchById(Long commentId) {
        return feedCommentJpaRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("TODO"));
    }

    @Override
    public void batchUpdateFeedCommentLikeCount(List<Long> commentIds, List<Long> likeCounts) {
        // 배치 업데이트 로직 구현
        QFeedComment feedComment = QFeedComment.feedComment;

        // 배치 업데이트 로직 구현
        if(commentIds == null || likeCounts == null || commentIds.size() != likeCounts.size()) {
            log.debug("피드 댓글 좋아요 수 배치 업데이트 할 것이 없음");
            return;
        }

        CaseBuilder.Cases<Long, NumberExpression<Long>> likeCountCase =
                new CaseBuilder()
                        .when(feedComment.id.eq(commentIds.get(0)))
                        .then(likeCounts.get(0));

        for(int i = 1; i < commentIds.size(); i++) {
            Long commentId = commentIds.get(i);
            Long likeCount = likeCounts.get(i);
            likeCountCase.when(feedComment.id.eq(commentId)).then(likeCount);
        }


        queryFactory
                .update(feedComment)
                .set(feedComment.likeCount, likeCountCase.otherwise(feedComment.likeCount)) // 기존 좋아요 수를 유지
                .where(feedComment.id.in(commentIds))
                .execute();
    }
}
