package com.lion.be.acceptance.util;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;

import java.util.List;
import java.util.Map;

public enum UserFixture {

    회원_원준("wj1234@gmail.com", "정원준", "https://www"), 비회원("asd1234@naver.com", "성이름", "https://www");

    private String email;
    private String name;
    private String imageUrl;

    UserFixture(String email, String name, String imageUrl) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
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

    public static Map<String, Object> 회원_멋사_온보딩_요청(){
        return Map.of(
                "userPhotos", List.of("photo1.jpg", "photo2.jpg"),
                "gender", Gender.MEN,
                "universityName", "멋사대학교",
                "position", Position.BACKEND,
                "mbti", Mbti.INFJ
        );
    }
    public static Map<String, Object> 회원_멋사2_온보딩_요청() {
        return Map.of(
                "userPhotos", List.of("photo1.jpg"),
                "gender", Gender.WOMEN,
                "universityName", "멋사대학교",
                "position", Position.BACKEND,
                "mbti", Mbti.ENFJ
        );
    }

}
