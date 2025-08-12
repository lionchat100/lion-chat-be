-- 더미 유저 20명 생성 (클러스터링 테스트용)
-- 클러스터 1: ENFP/ENTP + FRONTEND 계열 (외향적 + 프론트엔드)
-- 클러스터 2: INTJ/INTP + BACKEND 계열 (내향적 + 백엔드)
-- 클러스터 3: 혼합형 (다양한 조합)

-- AUTO_INCREMENT 초기화 후 데이터 삽입
ALTER TABLE member AUTO_INCREMENT = 1;

-- === 클러스터 1: 외향적 + 프론트엔드 계열 (5명) ===
INSERT INTO member (id, name, nickname, email, image_url, role, gender, university, position, mbti, onboarding_status, bio, is_university_view, cluster_id, preference_type, created_at, updated_at)
VALUES
    (1, '김프론트', '프론트마스터', 'front1@test.com', 'https://test.com/image1.jpg', 'USER', 'WOMEN', 'SEOUL', 'FRONTEND', 'ENFP', 'COMPLETED', '안녕하세요! 프론트엔드 개발자입니다.', true, 1, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (2, '이리액트', '리액트킹', 'front2@test.com', 'https://test.com/image2.jpg', 'USER', 'MEN', 'YONSEI_SINCHON', 'FRONTEND', 'ENFP', 'COMPLETED', 'React 전문 개발자입니다!', true, 1, 'POSITION_FOCUSED', NOW(), NOW()),
    (3, '박뷰js', '뷰제이에스', 'front3@test.com', 'https://test.com/image3.jpg', 'USER', 'WOMEN', 'SKKU', 'FRONTEND', 'ENTP', 'COMPLETED', 'Vue.js로 멋진 웹을 만들어요', true, 1, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (4, '최유엑스', 'UX디자이너', 'ux1@test.com', 'https://test.com/image4.jpg', 'USER', 'WOMEN', 'EWHA', 'UX_UI', 'ENFP', 'COMPLETED', 'UX/UI 디자이너로 사용자 경험을 설계합니다', true, 1, 'POSITION_FOCUSED', NOW(), NOW()),
    (5, '정디자인', '창의디자이너', 'ux2@test.com', 'https://test.com/image5.jpg', 'USER', 'MEN', 'HONGIK', 'UX_UI', 'ENTP', 'COMPLETED', '창의적인 디자인으로 세상을 바꿔요', true, 1, 'PREFERENCE_FOCUSED', NOW(), NOW()),

-- === 클러스터 2: 내향적 + 백엔드 계열 (5명) ===
    (6, '김백엔드', '백엔드전문가', 'back1@test.com', 'https://test.com/image6.jpg', 'USER', 'MEN', 'SEOUL', 'BACKEND', 'INTJ', 'COMPLETED', '서버 개발 전문가입니다', false, 2, 'POSITION_FOCUSED', NOW(), NOW()),
    (7, '이스프링', '스프링마스터', 'back2@test.com', 'https://test.com/image7.jpg', 'USER', 'MEN', 'YONSEI_SINCHON', 'BACKEND', 'INTJ', 'COMPLETED', 'Spring Framework 마스터', false, 2, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (8, '박자바', '자바개발자', 'back3@test.com', 'https://test.com/image8.jpg', 'USER', 'WOMEN', 'SEOUL', 'BACKEND', 'INTP', 'COMPLETED', 'Java로 견고한 시스템을 구축해요', true, 2, 'POSITION_FOCUSED', NOW(), NOW()),
    (9, '최노드', '노드제이에스', 'back4@test.com', 'https://test.com/image9.jpg', 'USER', 'MEN', 'YONSEI_SINCHON', 'BACKEND', 'INTP', 'COMPLETED', 'Node.js 백엔드 개발자', false, 2, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (10, '정파이썬', '파이썬데이터', 'back5@test.com', 'https://test.com/image10.jpg', 'USER', 'WOMEN', 'SKKU', 'BACKEND', 'INTJ', 'COMPLETED', 'Python으로 데이터를 다루는 개발자', true, 2, 'POSITION_FOCUSED', NOW(), NOW()),

-- === 클러스터 3: 혼합형 - 다양한 조합 (10명) ===
    (11, '김풀스택', '풀스택개발자', 'full1@test.com', 'https://test.com/image11.jpg', 'USER', 'MEN', 'CAU', 'FULLSTACK', 'ENTJ', 'COMPLETED', '풀스택 개발자로 전체를 아우르는 개발을 해요', true, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (12, '이매니저', '프로젝트매니저', 'pm1@test.com', 'https://test.com/image12.jpg', 'USER', 'WOMEN', 'SOGANG', 'PM', 'ESTJ', 'COMPLETED', '프로젝트 관리 전문가입니다', true, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (13, '박기획', 'PM리더', 'pm2@test.com', 'https://test.com/image13.jpg', 'USER', 'MEN', 'HANYANG_ERICA', 'PM', 'ENFJ', 'COMPLETED', '사람과 기술을 연결하는 PM', false, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (14, '최올라운드', '올라운더', 'full2@test.com', 'https://test.com/image14.jpg', 'USER', 'WOMEN', 'INHA', 'FULLSTACK', 'INFP', 'COMPLETED', '창의적인 올라운드 개발자', true, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (15, '정크리에이터', '감성디자이너', 'ux3@test.com', 'https://test.com/image15.jpg', 'USER', 'WOMEN', 'SUNGSHIN', 'UX_UI', 'ISFP', 'COMPLETED', '감성적인 디자인을 추구해요', true, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (16, '김개발자', '안정개발자', 'dev1@test.com', 'https://test.com/image16.jpg', 'USER', 'MEN', 'KOOKMIN', 'BACKEND', 'ISTJ', 'COMPLETED', '꼼꼼하고 안정적인 개발을 지향합니다', false, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (17, '이웹개발', '즐거운개발자', 'web1@test.com', 'https://test.com/image17.jpg', 'USER', 'WOMEN', 'DONGDUK', 'FRONTEND', 'ESFP', 'COMPLETED', '즐겁게 웹 개발하는 개발자', true, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (18, '박멀티', '융합개발자', 'multi1@test.com', 'https://test.com/image18.jpg', 'USER', 'MEN', 'SOOKMYUNG', 'FULLSTACK', 'ESFJ', 'COMPLETED', '다양한 기술을 융합하는 개발자', true, 3, 'POSITION_FOCUSED', NOW(), NOW()),
    (19, '최리더', '액션리더', 'leader1@test.com', 'https://test.com/image19.jpg', 'USER', 'WOMEN', 'EWHA', 'PM', 'ESTP', 'COMPLETED', '액션형 프로젝트 리더', false, 3, 'PREFERENCE_FOCUSED', NOW(), NOW()),
    (20, '정올인원', '효율개발자', 'all1@test.com', 'https://test.com/image20.jpg', 'USER', 'MEN', 'SEOUL', 'FULLSTACK', 'ISTP', 'COMPLETED', '실용적이고 효율적인 개발을 추구합니다', true, 3, 'CAREER_FOCUSED', NOW(), NOW());

-- 유저 사진 데이터 (각 유저당 1-3장)
INSERT INTO user_photo (user_id, image_url, order_index, created_at, updated_at)
VALUES
-- 클러스터 1 사용자들 사진
(1, 'https://test.com/photo1-1.jpg', 1, NOW(), NOW()),
(1, 'https://test.com/photo1-2.jpg', 2, NOW(), NOW()),
(2, 'https://test.com/photo2-1.jpg', 1, NOW(), NOW()),
(2, 'https://test.com/photo2-2.jpg', 2, NOW(), NOW()),
(2, 'https://test.com/photo2-3.jpg', 3, NOW(), NOW()),
(3, 'https://test.com/photo3-1.jpg', 1, NOW(), NOW()),
(4, 'https://test.com/photo4-1.jpg', 1, NOW(), NOW()),
(4, 'https://test.com/photo4-2.jpg', 2, NOW(), NOW()),
(5, 'https://test.com/photo5-1.jpg', 1, NOW(), NOW()),

-- 클러스터 2 사용자들 사진
(6, 'https://test.com/photo6-1.jpg', 1, NOW(), NOW()),
(6, 'https://test.com/photo6-2.jpg', 2, NOW(), NOW()),
(7, 'https://test.com/photo7-1.jpg', 1, NOW(), NOW()),
(8, 'https://test.com/photo8-1.jpg', 1, NOW(), NOW()),
(8, 'https://test.com/photo8-2.jpg', 2, NOW(), NOW()),
(8, 'https://test.com/photo8-3.jpg', 3, NOW(), NOW()),
(9, 'https://test.com/photo9-1.jpg', 1, NOW(), NOW()),
(10, 'https://test.com/photo10-1.jpg', 1, NOW(), NOW()),
(10, 'https://test.com/photo10-2.jpg', 2, NOW(), NOW()),

-- 클러스터 3 사용자들 사진
(11, 'https://test.com/photo11-1.jpg', 1, NOW(), NOW()),
(12, 'https://test.com/photo12-1.jpg', 1, NOW(), NOW()),
(12, 'https://test.com/photo12-2.jpg', 2, NOW(), NOW()),
(13, 'https://test.com/photo13-1.jpg', 1, NOW(), NOW()),
(14, 'https://test.com/photo14-1.jpg', 1, NOW(), NOW()),
(14, 'https://test.com/photo14-2.jpg', 2, NOW(), NOW()),
(14, 'https://test.com/photo14-3.jpg', 3, NOW(), NOW()),
(15, 'https://test.com/photo15-1.jpg', 1, NOW(), NOW()),
(16, 'https://test.com/photo16-1.jpg', 1, NOW(), NOW()),
(17, 'https://test.com/photo17-1.jpg', 1, NOW(), NOW()),
(17, 'https://test.com/photo17-2.jpg', 2, NOW(), NOW()),
(18, 'https://test.com/photo18-1.jpg', 1, NOW(), NOW()),
(19, 'https://test.com/photo19-1.jpg', 1, NOW(), NOW()),
(20, 'https://test.com/photo20-1.jpg', 1, NOW(), NOW()),
(20, 'https://test.com/photo20-2.jpg', 2, NOW(), NOW());

-- 약관 동의 데이터
INSERT INTO agreements (user_id, agreement_type, agreed)
VALUES
-- 모든 사용자에 대해 필수 약관 동의
(1, 'REQUIRED', true),
(1, 'MARKETING', false),
(2, 'REQUIRED', true),
(2, 'MARKETING', true),
(3, 'REQUIRED', true),
(3, 'MARKETING', false),
(4, 'REQUIRED', true),
(4, 'MARKETING', true),
(5, 'REQUIRED', true),
(5, 'MARKETING', false),
(6, 'REQUIRED', true),
(6, 'MARKETING', false),
(7, 'REQUIRED', true),
(7, 'MARKETING', true),
(8, 'REQUIRED', true),
(8, 'MARKETING', false),
(9, 'REQUIRED', true),
(9, 'MARKETING', true),
(10, 'REQUIRED', true),
(10, 'MARKETING', false),
(11, 'REQUIRED', true),
(11, 'MARKETING', true),
(12, 'REQUIRED', true),
(12, 'MARKETING', false),
(13, 'REQUIRED', true),
(13, 'MARKETING', true),
(14, 'REQUIRED', true),
(14, 'MARKETING', false),
(15, 'REQUIRED', true),
(15, 'MARKETING', true),
(16, 'REQUIRED', true),
(16, 'MARKETING', false),
(17, 'REQUIRED', true),
(17, 'MARKETING', true),
(18, 'REQUIRED', true),
(18, 'MARKETING', false),
(19, 'REQUIRED', true),
(19, 'MARKETING', true),
(20, 'REQUIRED', true),
(20, 'MARKETING', false);
