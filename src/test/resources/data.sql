-- AUTO_INCREMENT 초기화 후 데이터 삽입
ALTER TABLE member AUTO_INCREMENT = 1;
ALTER TABLE image AUTO_INCREMENT = 1;

-- === 테스트용 유저 (원준, 토킷) - 온보딩 전 상태 ===
INSERT INTO member (id, name, email, image_url, role, created_at, updated_at)
VALUES
    (1, '정원준', 'wj1234@gmail.com', 'https://www', 'USER', NOW(), NOW()),
    (2, '김토킷', 'tokit@gmail.com', 'https://www', 'USER', NOW(), NOW());

-- === 더미 유저 20명 생성 (클러스터링 테스트용) ===
INSERT INTO member (id, name, nickname, email, image_url, role, gender, university, position, mbti, onboarding_status, bio, is_university_view, cluster_id, preference_type, created_at, updated_at)
VALUES
    -- 클러스터 1: 외향적 + 프론트엔드 계열 (5명)
    (3, '김프론트', '프론트마스터', 'front1@test.com', 'https://test.com/image1.jpg', 'USER', 'WOMEN', 'SEOUL', 'FRONTEND', 'ENFP', 'COMPLETED', '안녕하세요! 프론트엔드 개발자입니다.', true, 1, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (4, '이리액트', '리액트킹', 'front2@test.com', 'https://test.com/image2.jpg', 'USER', 'MEN', 'YONSEI_SINCHON', 'FRONTEND', 'ENFP', 'COMPLETED', 'React 전문 개발자입니다!', true, 1, 'POSITION_FOCUSED', NOW(), NOW()),
    (5, '박뷰js', '뷰제이에스', 'front3@test.com', 'https://test.com/image3.jpg', 'USER', 'WOMEN', 'SKKU', 'FRONTEND', 'ENTP', 'COMPLETED', 'Vue.js로 멋진 웹을 만들어요', true, 1, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (6, '최유엑스', 'UX디자이너', 'ux1@test.com', 'https://test.com/image4.jpg', 'USER', 'WOMEN', 'EWHA', 'UX_UI', 'ENFP', 'COMPLETED', 'UX/UI 디자이너로 사용자 경험을 설계합니다', true, 1, 'POSITION_FOCUSED', NOW(), NOW()),
    (7, '정디자인', '창의디자이너', 'ux2@test.com', 'https://test.com/image5.jpg', 'USER', 'MEN', 'HONGIK', 'UX_UI', 'ENTP', 'COMPLETED', '창의적인 디자인으로 세상을 바꿔요', true, 1, 'PREFERENCE_FOCUSED', NOW(), NOW()),

    -- 클러스터 2: 내향적 + 백엔드 계열 (5명)
    (8, '김백엔드', '백엔드전문가', 'back1@test.com', 'https://test.com/image6.jpg', 'USER', 'MEN', 'SEOUL', 'BACKEND', 'INTJ', 'COMPLETED', '서버 개발 전문가입니다', false, 2, 'POSITION_FOCUSED', NOW(), NOW()),
    (9, '이스프링', '스프링마스터', 'back2@test.com', 'https://test.com/image7.jpg', 'USER', 'MEN', 'YONSEI_SINCHON', 'BACKEND', 'INTJ', 'COMPLETED', 'Spring Framework 마스터', false, 2, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (10, '박자바', '자바개발자', 'back3@test.com', 'https://test.com/image8.jpg', 'USER', 'WOMEN', 'SEOUL', 'BACKEND', 'INTP', 'COMPLETED', 'Java로 견고한 시스템을 구축해요', true, 2, 'POSITION_FOCUSED', NOW(), NOW()),
    (11, '최노드', '노드제이에스', 'back4@test.com', 'https://test.com/image9.jpg', 'USER', 'MEN', 'YONSEI_SINCHON', 'BACKEND', 'INTP', 'COMPLETED', 'Node.js 백엔드 개발자', false, 2, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (12, '정파이썬', '파이썬데이터', 'back5@test.com', 'https://test.com/image10.jpg', 'USER', 'WOMEN', 'SKKU', 'BACKEND', 'INTJ', 'COMPLETED', 'Python으로 데이터를 다루는 개발자', true, 2, 'POSITION_FOCUSED', NOW(), NOW()),

    -- 클러스터 3: 혼합형 - 다양한 조합 (10명)
    (13, '김풀스택', '풀스택개발자', 'full1@test.com', 'https://test.com/image11.jpg', 'USER', 'MEN', 'CAU', 'FULLSTACK', 'ENTJ', 'COMPLETED', '풀스택 개발자로 전체를 아우르는 개발을 해요', true, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (14, '이매니저', '프로젝트매니저', 'pm1@test.com', 'https://test.com/image12.jpg', 'USER', 'WOMEN', 'SOGANG', 'PM', 'ESTJ', 'COMPLETED', '프로젝트 관리 전문가입니다', true, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (15, '박기획', 'PM리더', 'pm2@test.com', 'https://test.com/image13.jpg', 'USER', 'MEN', 'HANYANG_ERICA', 'PM', 'ENFJ', 'COMPLETED', '사람과 기술을 연결하는 PM', false, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (16, '최올라운드', '올라운더', 'full2@test.com', 'https://test.com/image14.jpg', 'USER', 'WOMEN', 'INHA', 'FULLSTACK', 'INFP', 'COMPLETED', '창의적인 올라운드 개발자', true, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (17, '정크리에이터', '감성디자이너', 'ux3@test.com', 'https://test.com/image15.jpg', 'USER', 'WOMEN', 'SUNGSHIN', 'UX_UI', 'ISFP', 'COMPLETED', '감성적인 디자인을 추구해요', true, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (18, '김개발자', '안정개발자', 'dev1@test.com', 'https://test.com/image16.jpg', 'USER', 'MEN', 'KOOKMIN', 'BACKEND', 'ISTJ', 'COMPLETED', '꼼꼼하고 안정적인 개발을 지향합니다', false, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (19, '이웹개발', '즐거운개발자', 'web1@test.com', 'https://test.com/image17.jpg', 'USER', 'WOMEN', 'DONGDUK', 'FRONTEND', 'ESFP', 'COMPLETED', '즐겁게 웹 개발하는 개발자', true, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (20, '박멀티', '융합개발자', 'multi1@test.com', 'https://test.com/image18.jpg', 'USER', 'MEN', 'SOOKMYUNG', 'FULLSTACK', 'ESFJ', 'COMPLETED', '다양한 기술을 융합하는 개발자', true, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (21, '최리더', '액션리더', 'leader1@test.com', 'https://test.com/image19.jpg', 'USER', 'WOMEN', 'EWHA', 'PM', 'ESTP', 'COMPLETED', '액션형 프로젝트 리더', false, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (22, '정올인원', '효율개발자', 'all1@test.com', 'https://test.com/image20.jpg', 'USER', 'MEN', 'SEOUL', 'FULLSTACK', 'ISTP', 'COMPLETED', '실용적이고 효율적인 개발을 추구합니다', true, 3, 'CAREER_FOCUSED', NOW(), NOW());

-- === 더미 유저용 이미지 데이터 (각 유저마다 최소 1장 이상) ===
INSERT INTO image (id, original_file_name, stored_file_name, image_url, uploader_id, is_deleted, created_at, updated_at)
VALUES
    -- 클러스터 1 사용자들 이미지
    (1, 'front1-1.jpg', 'uuid1_front1-1.jpg', 'https://test.com/photo1-1.jpg', 3, false, NOW(), NOW()),
    (2, 'front1-2.jpg', 'uuid2_front1-2.jpg', 'https://test.com/photo1-2.jpg', 3, false, NOW(), NOW()),
    (3, 'front2-1.jpg', 'uuid3_front2-1.jpg', 'https://test.com/photo2-1.jpg', 4, false, NOW(), NOW()),
    (4, 'front2-2.jpg', 'uuid4_front2-2.jpg', 'https://test.com/photo2-2.jpg', 4, false, NOW(), NOW()),
    (5, 'front2-3.jpg', 'uuid5_front2-3.jpg', 'https://test.com/photo2-3.jpg', 4, false, NOW(), NOW()),
    (6, 'front3-1.jpg', 'uuid6_front3-1.jpg', 'https://test.com/photo3-1.jpg', 5, false, NOW(), NOW()),
    (7, 'ux1-1.jpg', 'uuid7_ux1-1.jpg', 'https://test.com/photo4-1.jpg', 6, false, NOW(), NOW()),
    (8, 'ux1-2.jpg', 'uuid8_ux1-2.jpg', 'https://test.com/photo4-2.jpg', 6, false, NOW(), NOW()),
    (9, 'ux2-1.jpg', 'uuid9_ux2-1.jpg', 'https://test.com/photo5-1.jpg', 7, false, NOW(), NOW()),

    -- 클러스터 2 사용자들 이미지
    (10, 'back1-1.jpg', 'uuid10_back1-1.jpg', 'https://test.com/photo6-1.jpg', 8, false, NOW(), NOW()),
    (11, 'back1-2.jpg', 'uuid11_back1-2.jpg', 'https://test.com/photo6-2.jpg', 8, false, NOW(), NOW()),
    (12, 'back2-1.jpg', 'uuid12_back2-1.jpg', 'https://test.com/photo7-1.jpg', 9, false, NOW(), NOW()),
    (13, 'back3-1.jpg', 'uuid13_back3-1.jpg', 'https://test.com/photo8-1.jpg', 10, false, NOW(), NOW()),
    (14, 'back3-2.jpg', 'uuid14_back3-2.jpg', 'https://test.com/photo8-2.jpg', 10, false, NOW(), NOW()),
    (15, 'back3-3.jpg', 'uuid15_back3-3.jpg', 'https://test.com/photo8-3.jpg', 10, false, NOW(), NOW()),
    (16, 'back4-1.jpg', 'uuid16_back4-1.jpg', 'https://test.com/photo9-1.jpg', 11, false, NOW(), NOW()),
    (17, 'back5-1.jpg', 'uuid17_back5-1.jpg', 'https://test.com/photo10-1.jpg', 12, false, NOW(), NOW()),
    (18, 'back5-2.jpg', 'uuid18_back5-2.jpg', 'https://test.com/photo10-2.jpg', 12, false, NOW(), NOW()),

    -- 클러스터 3 사용자들 이미지 (각자 최소 1장씩)
    (19, 'full1-1.jpg', 'uuid19_full1-1.jpg', 'https://test.com/photo11-1.jpg', 13, false, NOW(), NOW()),
    (20, 'pm1-1.jpg', 'uuid20_pm1-1.jpg', 'https://test.com/photo12-1.jpg', 14, false, NOW(), NOW()),
    (21, 'pm1-2.jpg', 'uuid21_pm1-2.jpg', 'https://test.com/photo12-2.jpg', 14, false, NOW(), NOW()),
    (22, 'pm2-1.jpg', 'uuid22_pm2-1.jpg', 'https://test.com/photo13-1.jpg', 15, false, NOW(), NOW()),
    (23, 'full2-1.jpg', 'uuid23_full2-1.jpg', 'https://test.com/photo14-1.jpg', 16, false, NOW(), NOW()),
    (24, 'full2-2.jpg', 'uuid24_full2-2.jpg', 'https://test.com/photo14-2.jpg', 16, false, NOW(), NOW()),
    (25, 'full2-3.jpg', 'uuid25_full2-3.jpg', 'https://test.com/photo14-3.jpg', 16, false, NOW(), NOW()),
    (26, 'ux3-1.jpg', 'uuid26_ux3-1.jpg', 'https://test.com/photo15-1.jpg', 17, false, NOW(), NOW()),
    (27, 'dev1-1.jpg', 'uuid27_dev1-1.jpg', 'https://test.com/photo16-1.jpg', 18, false, NOW(), NOW()),
    (28, 'web1-1.jpg', 'uuid28_web1-1.jpg', 'https://test.com/photo17-1.jpg', 19, false, NOW(), NOW()),
    (29, 'web1-2.jpg', 'uuid29_web1-2.jpg', 'https://test.com/photo17-2.jpg', 19, false, NOW(), NOW()),
    (30, 'multi1-1.jpg', 'uuid30_multi1-1.jpg', 'https://test.com/photo18-1.jpg', 20, false, NOW(), NOW()),
    (31, 'leader1-1.jpg', 'uuid31_leader1-1.jpg', 'https://test.com/photo19-1.jpg', 21, false, NOW(), NOW()),
    (32, 'all1-1.jpg', 'uuid32_all1-1.jpg', 'https://test.com/photo20-1.jpg', 22, false, NOW(), NOW()),
    (33, 'all1-2.jpg', 'uuid33_all1-2.jpg', 'https://test.com/photo20-2.jpg', 22, false, NOW(), NOW());

-- === UserPhoto 중간 테이블 데이터 (온보딩 완료한 사용자들만) ===
INSERT INTO user_photo (user_id, image_id, order_index, created_at, updated_at)
VALUES
    -- 클러스터 1 사용자들 사진 연결
    (3, 1, 1, NOW(), NOW()),  -- 김프론트
    (3, 2, 2, NOW(), NOW()),
    (4, 3, 1, NOW(), NOW()),  -- 이리액트
    (4, 4, 2, NOW(), NOW()),
    (4, 5, 3, NOW(), NOW()),
    (5, 6, 1, NOW(), NOW()),  -- 박뷰js
    (6, 7, 1, NOW(), NOW()),  -- 최유엑스
    (6, 8, 2, NOW(), NOW()),
    (7, 9, 1, NOW(), NOW()),  -- 정디자인

    -- 클러스터 2 사용자들 사진 연결
    (8, 10, 1, NOW(), NOW()), -- 김백엔드
    (8, 11, 2, NOW(), NOW()),
    (9, 12, 1, NOW(), NOW()), -- 이스프링
    (10, 13, 1, NOW(), NOW()), -- 박자바
    (10, 14, 2, NOW(), NOW()),
    (10, 15, 3, NOW(), NOW()),
    (11, 16, 1, NOW(), NOW()), -- 최노드
    (12, 17, 1, NOW(), NOW()), -- 정파이썬
    (12, 18, 2, NOW(), NOW()),

    -- 클러스터 3 사용자들 사진 연결 (모든 사용자 최소 1장씩)
    (13, 19, 1, NOW(), NOW()), -- 김풀스택
    (14, 20, 1, NOW(), NOW()), -- 이매니저
    (14, 21, 2, NOW(), NOW()),
    (15, 22, 1, NOW(), NOW()), -- 박기획
    (16, 23, 1, NOW(), NOW()), -- 최올라운드
    (16, 24, 2, NOW(), NOW()),
    (16, 25, 3, NOW(), NOW()),
    (17, 26, 1, NOW(), NOW()), -- 정크리에이터
    (18, 27, 1, NOW(), NOW()), -- 김개발자
    (19, 28, 1, NOW(), NOW()), -- 이웹개발
    (19, 29, 2, NOW(), NOW()),
    (20, 30, 1, NOW(), NOW()), -- 박멀티
    (21, 31, 1, NOW(), NOW()), -- 최리더
    (22, 32, 1, NOW(), NOW()), -- 정올인원
    (22, 33, 2, NOW(), NOW());

-- === 약관 동의 데이터 (온보딩 완료한 사용자들만) ===
INSERT INTO agreements (user_id, agreement_type, agreed)
VALUES
    -- 클러스터 1 사용자들
    (3, 'REQUIRED', true),   -- 김프론트
    (3, 'MARKETING', false),
    (4, 'REQUIRED', true),   -- 이리액트
    (4, 'MARKETING', true),
    (5, 'REQUIRED', true),   -- 박뷰js
    (5, 'MARKETING', false),
    (6, 'REQUIRED', true),   -- 최유엑스
    (6, 'MARKETING', true),
    (7, 'REQUIRED', true),   -- 정디자인
    (7, 'MARKETING', false),

    -- 클러스터 2 사용자들
    (8, 'REQUIRED', true),   -- 김백엔드
    (8, 'MARKETING', false),
    (9, 'REQUIRED', true),   -- 이스프링
    (9, 'MARKETING', true),
    (10, 'REQUIRED', true),  -- 박자바
    (10, 'MARKETING', false),
    (11, 'REQUIRED', true),  -- 최노드
    (11, 'MARKETING', true),
    (12, 'REQUIRED', true),  -- 정파이썬
    (12, 'MARKETING', false),

    -- 클러스터 3 사용자들
    (13, 'REQUIRED', true),  -- 김풀스택
    (13, 'MARKETING', true),
    (14, 'REQUIRED', true),  -- 이매니저
    (14, 'MARKETING', false),
    (15, 'REQUIRED', true),  -- 박기획
    (15, 'MARKETING', true),
    (16, 'REQUIRED', true),  -- 최올라운드
    (16, 'MARKETING', false),
    (17, 'REQUIRED', true),  -- 정크리에이터
    (17, 'MARKETING', true),
    (18, 'REQUIRED', true),  -- 김개발자
    (18, 'MARKETING', false),
    (19, 'REQUIRED', true),  -- 이웹개발
    (19, 'MARKETING', true),
    (20, 'REQUIRED', true),  -- 박멀티
    (20, 'MARKETING', false),
    (21, 'REQUIRED', true),  -- 최리더
    (21, 'MARKETING', true),
    (22, 'REQUIRED', true),  -- 정올인원
    (22, 'MARKETING', false);
