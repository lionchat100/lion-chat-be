package com.lion.be.acceptance.util;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.PreferenceType;
import com.lion.be.user.domain.University;

import java.util.List;
import java.util.Map;

public enum UserFixture {

    회원_원준("wj1234@gmail.com", "정원준", "https://www", false), 비회원("asd1234@naver.com", "성이름", "https://www", false);

    private String email;
    private String name;
    private String imageUrl;
	private boolean isOnboardingCompleted;

    UserFixture(String email, String name, String imageUrl, boolean isOnboardingCompleted) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
		this.isOnboardingCompleted = isOnboardingCompleted;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static Map<String, Object> 사용자_원준_회원가입_요청() {
        return Map.of("email", 회원_원준.email, "name", 회원_원준.name, "imageUrl", 회원_원준.imageUrl);
    }

    public static Map<String, Object> 비회원_회원가입_요청() {
        return Map.of("email", 비회원.email, "name", 비회원.name, "imageUrl", 비회원.imageUrl);
    }

	public static Map<String, Object> 회원_멋사_온보딩_요청(){
		return Map.ofEntries(
			Map.entry("nickname", "멋진 호랑이"),
			Map.entry("userPhotos", List.of("photo1.jpg", "photo2.jpg")),
			Map.entry("gender", Gender.MEN),
			Map.entry("university", University.LIKELION),
			Map.entry("position", Position.FRONTEND),
			Map.entry("mbti", Mbti.ENFJ),
			Map.entry("bio", "안녕하세요 멋진호랑이 입니다."),
			Map.entry("requiredAgreements", true),
			Map.entry("marketingAgreement", false),
			Map.entry("isUniversityView", true),
			Map.entry("preferenceType", PreferenceType.MBTI_FOCUSED)
		);
	}

	public static Map<String, Object> 회원_멋사2_온보딩_요청() {
		return Map.ofEntries(
			Map.entry("nickname", "멋진 사자"),
			Map.entry("userPhotos", List.of("photo1.jpg")),
			Map.entry("gender", Gender.WOMEN),
			Map.entry("university", University.LIKELION),
			Map.entry("position", Position.BACKEND),
			Map.entry("mbti", Mbti.ENFP),
			Map.entry("bio", "안녕하세요 멋진사자 입니다."),
			Map.entry("requiredAgreements", true),
			Map.entry("marketingAgreement", false),
			Map.entry("isUniversityView", true),
			Map.entry("preferenceType", PreferenceType.MBTI_FOCUSED)
		);
	}

}
