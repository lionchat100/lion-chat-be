package com.lion.be.global.exception;

public class FeedNotFoundException extends CustomException{
    public FeedNotFoundException() {
        super(ErrorCode.FEED_NOT_FOUND);
    }
}
