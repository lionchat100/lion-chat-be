package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.repository.FeedCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedCommentWriteService {

    private final FeedCommentRepository feedCommentRepository;

}
