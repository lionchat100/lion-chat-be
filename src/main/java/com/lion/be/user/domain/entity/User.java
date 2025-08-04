package com.lion.be.user.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.global.entity.BaseEntity;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.OnboardingStatus;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.dto.OnboardingData;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPhoto> userPhotos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus onboardingStatus = OnboardingStatus.PENDING;

    public User(String name, String email, String imageUrl, Role role) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void addChatRoomUser(ChatRoomUser chatRoomUser) {
        chatRoomUsers.add(chatRoomUser);
    }

    public void completeOnboarding(OnboardingData data, University university) {
        validateOnboardingPreconditions();
        validateOnboardingData(data);
        this.gender = data.getGender();
        this.university = university;
        this.position = data.getPosition();
        this.mbti = data.getMbti();

        for (int i = 0; i < data.getUserPhotos().size(); i++) {
            this.userPhotos.add(new UserPhoto(this, data.getUserPhotos().get(i), i + 1));
        }
        this.onboardingStatus = OnboardingStatus.COMPLETED;
    }

    public boolean isOnboardingCompleted() {
        return onboardingStatus == OnboardingStatus.COMPLETED;
    }

    private void validateOnboardingPreconditions() {
        if (isOnboardingCompleted()) {
            throw new IllegalStateException("이미 온보딩이 완료된 사용자입니다.");
        }
        if (this.name == null || this.email == null) {
            throw new IllegalStateException("기본 사용자 정보가 누락되었습니다.");
        }
    }

    private void validateOnboardingData(OnboardingData data) {
        if (data.getGender() == null || data.getUniversityName() == null ||
                data.getPosition() == null || data.getMbti() == null || data.getUserPhotos() == null) {
            throw new IllegalArgumentException("온보딩 필수 정보가 누락되었습니다.");
        }
        if (data.getUserPhotos().isEmpty()) {
            throw new IllegalArgumentException("최소 1장의 사진이 필요합니다.");
        }
        if (data.getUserPhotos().size() > 3) {
            throw new IllegalArgumentException("사진은 최대 3장까지 업로드 가능합니다.");
        }
    }

}
