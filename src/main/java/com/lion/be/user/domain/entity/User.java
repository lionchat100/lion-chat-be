package com.lion.be.user.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.global.entity.BaseEntity;
import com.lion.be.user.domain.AgreementType;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.OnboardingStatus;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.University;
import com.lion.be.user.domain.entity.dto.OnboardingData;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @Enumerated(EnumType.STRING)
    private University university;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Mbti mbti;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus onboardingStatus = OnboardingStatus.PENDING;

    private String bio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Agreements> agreements = new ArrayList<>();

    private Boolean isUniversityView;

    /**
     * K-means 클러스터링으로 배정된 클러스터 ID
     * - 유사한 성향(MBTI + Position)의 사용자들을 그룹핑
     * - 추천 시스템에서 같은 클러스터 내 사용자 우선 노출
     * - 온보딩 완료 시점에 자동 배정
     */
    @Column(name = "cluster_id")
    private Integer clusterId;

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

    public void completeOnboarding(OnboardingData data) {
        validateOnboardingPreconditions();
        validateOnboardingData(data);
        this.gender = data.gender();
        this.university = data.university();
        this.position = data.position();
        this.mbti = data.mbti();
        this.bio = data.bio();
        this.agreements.add(new Agreements(this, AgreementType.REQUIRED, data.requiredAgreements()));
        this.agreements.add(new Agreements(this, AgreementType.MARKETING, data.marketingAgreements()));
        this.isUniversityView = data.isUniversityView();


        for (int i = 0; i < data.userPhotos().size(); i++) {
            this.userPhotos.add(new UserPhoto(this, data.userPhotos().get(i), i + 1));
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
        if (data.gender() == null || data.university() == null || data.position() == null ||
                 data.mbti() == null || data.userPhotos() == null) {
            throw new IllegalArgumentException("온보딩 필수 정보가 누락되었습니다.");
        }
        if (data.userPhotos().isEmpty()) {
            throw new IllegalArgumentException("최소 1장의 사진이 필요합니다.");
        }
        if (data.userPhotos().size() > 3) {
            throw new IllegalArgumentException("사진은 최대 3장까지 업로드 가능합니다.");
        }
    }

    /**
     * 클러스터 ID를 배정합니다.
     * 온보딩 완료된 사용자에게만 배정 가능합니다.
     */
    public void assignToCluster(Integer clusterId) {
        if (!isOnboardingCompleted()) {
            throw new IllegalStateException("온보딩이 완료되지 않은 사용자는 클러스터 배정이 불가능합니다.");
        }
        if (clusterId == null || clusterId < 0) {
            throw new IllegalArgumentException("유효하지 않은 클러스터 ID입니다.");
        }
        this.clusterId = clusterId;
    }

}
