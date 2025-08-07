package com.lion.be.feed_comment.repository.persistence.jpa;

import com.lion.be.feed_comment.domain.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCommentJpaRepository extends JpaRepository<FeedComment, Long> {

}
