package com.lion.be.acceptance.util;

import static com.lion.be.acceptance.util.UserFixture.*;

import java.util.Map;

public class AuthFixture {

    public static Map<String, Object> 비회원_로그인_요청() {
        return Map.of(
                "email", 비회원.getEmail(),
                "name", 비회원.getName(),
                "imageUrl", 비회원.getImageUrl());
    }

    public static Map<String, Object> 사용자_원준_로그인_요청() {
        return Map.of(
                "email", 회원_원준.getEmail(),
                "name", 회원_원준.getName(),
                "imageUrl", 회원_원준.getImageUrl());
    }

	public static Map<String, Object> 사용자_토킷_로그인_요청() {
		return Map.of(
			"email", 회원_토킷.getEmail(),
			"name", 회원_토킷.getName(),
			"imageUrl", 회원_토킷.getImageUrl());
	}

	public static Map<String, Object> 어드민_멋사_로그인_요청() {
		return Map.of(
			"email", 어드민_멋사.getEmail(),
			"name", 어드민_멋사.getName(),
			"imageUrl", 어드민_멋사.getImageUrl());
	}

}
