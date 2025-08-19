package com.lion.be.global.util;

public final class RedisKey {
    private RedisKey() {
    }


    //피드 댓글 카운트에 대한 레디스 키
    public static final String DIRTY_COMMENT_COUNT_KEY = "dirty:comment_count";
    public static final String COMMENT_COUNT_KEY = "feed:comment_count:";

    //댓글 좋아요에 대한 레디스 키
    public static final String DIRTY_COMMENT_LIKE_KEY = "dirty:comment_like";
    public static final String COMMENT_LIKE_COUNT_KEY_PREFIX = "comment:like_count:";
    public static final String COMMENT_LIKED_USERS_KEY_PREFIX = "comment:liked_users:";

    //피드 좋아요에 대한 레디스 키
    public static final String DIRTY_FEED_LIKE_KEY = "dirty:feed_like";
    public static final String FEED_LIKE_COUNT_KEY_PREFIX = "feed:like_count:";
    public static final String FEED_LIKED_USERS_KEY_PREFIX = "feed:liked_users:";

    public static final String USER_LIKED_FEED_SET_PREFIX = "user:liked_feed:";
}
