# Tokit - IT 취업준비생을 위한 커피챗 네트워킹 플랫폼

<img width="1280" height="800" alt="github" src="https://github.com/user-attachments/assets/eb7fdb1d-9923-4cf4-8be0-3b8339df92ee" />

---

## 프로젝트 개요

Tokit은 개발자 문화의 하나인 커피챗(Coffee Chat)과 간편한 사용자 탐색 기능을 결합한 소셜 서비스입니다. <br/>
사용자는 관심 있는 직군을 필터링하여 스크롤 방식으로 다른 참여자를 탐색하고, 프로필에 '좋아요'를 남기거나 1:1 실시간 채팅을 시작할 수 있습니다. <br/>
또한 전체 사용자가 공유하는 피드에서 게시물을 작성하고, 댓글을 통해 자유롭게 소통할 수 있습니다.

Tokit은 멋쟁이사자처럼 전국 대학 해커톤 참가자들의 원활한 네트워킹을 위해 시작된 프로젝트입니다. <br/>
짧은 해커톤 환경에서도 다양한 참가자들이 자연스럽게 연결될 수 있도록 하는 것이 핵심 목표입니다.

- **개발 기간**: 약 3주 (기획·디자인 포함 전체 4주)
- **예상 사용자 수**: 최대 1,500명 (해커톤 참가자 기준)
- **핵심 목표**:
    - 단기간 MVP 구현
    - 실시간 채팅 및 탐색 기능 제공
    - 해커톤 현장에서의 원활한 커뮤니케이션 경험

---

## 기술적 핵심 기능

### 인증 & 온보딩

- OAuth 기반 회원가입 (카카오)
- 단계별 온보딩을 통한 프로필 등록

### 사용자 탐색

- 무한 스크롤 방식의 사용자 탐색
- 직군, 대학, MBTI 등 다중 필터링

### 실시간 커뮤니케이션

- WebSocket 기반 1:1 실시간 채팅
- 채팅방 목록 및 메시지 히스토리

### 모바일 최적화

- PWA 지원으로 네이티브 앱과 유사한 경험
- 반응형 디자인 및 터치 최적화

---

## 화면 구성

### 유저

<table>
  <tr>
    <td align="center"><strong>회원가입 & 온보딩</strong></td>
    <td align="center"><strong>유저 프로필</strong></td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" alt="OAuth_Onboarding" src="https://github.com/user-attachments/assets/d02fa792-680d-42d7-9e53-eccd952abf87" />
    </td>
    <td align="center">
      <img width="400" alt="Profile" src="https://github.com/user-attachments/assets/20a1012f-c04a-4998-919a-084178c54cff" />
    </td>
  </tr>
</table>

### 사용자 탐색

<table>
  <tr>
    <td align="center"><strong>사용자 탐색 & 좋아요</strong></td>
    <td align="center"><strong>좋아요한 사람</strong></td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" alt="Eplore_like" src="https://github.com/user-attachments/assets/d5fdc74b-7fd9-4b91-9b7e-82a05e1543cd" />
    </td>
    <td align="center">
      <img width="400" alt="Liked" src="https://github.com/user-attachments/assets/280247af-bd18-494f-9b75-f3496ee75223" />
    </td>
  </tr>
</table>

### 실시간 커뮤니케이션

<table>
  <tr>
    <td align="center"><strong>채팅</strong></td>
    <td align="center"><strong>게시판</strong></td>
    <td align="center"><strong>알림</strong></td>
  </tr>
  <tr>
    <td align="center">
      <img width="400" alt="Chat" src="https://github.com/user-attachments/assets/05b02d36-61bc-4356-8b6d-fbbeaabb8d04" />
    </td>
    <td align="center">
      <img width="400" alt="Board" src="https://github.com/user-attachments/assets/d087142a-0086-49fa-a0d1-5551a8a67e52" />
    </td>
      <td align="center">
      <img width="400" alt="Notify" src="https://github.com/user-attachments/assets/5858e370-fea8-4d25-adea-78390cfbe528" />
    </td>
  </tr>
</table>


---

## 코드 기여자

| FE-LD 박상민                                      | FE 송하                                    | FE 권영우                               |
|------------------------------------------------|------------------------------------------|--------------------------------------|
| [@sakedon2151](https://github.com/sakedon2151) | [@poan1221](https://github.com/poan1221) | [@kwonup](https://github.com/kwonup) |

| BE-LD 정원준                                    | BE 류승범                                 | BE 윤채민                                     | BE 이승헌                                       |
|----------------------------------------------|----------------------------------------|--------------------------------------------|----------------------------------------------|
| [@devbattery](https://github.com/devbattery) | [@W-llama](https://github.com/W-llama) | [@cinnamein](https://github.com/cinnamein) | [@lsh-kw0315](https://github.com/lsh-kw0315) |

---

## 아키텍처

### 인프라

<img width="7680" height="4320" alt="2d-architecture" src="https://github.com/user-attachments/assets/d92c6dcf-e1cb-4448-b8db-3ae9346392b8" />

### 유저

<img width="373" height="675" alt="스크린샷 2025-08-28 12 05 09" src="https://github.com/user-attachments/assets/ed08df05-e739-459c-9f33-6a93ce1926d7" />

### 채팅

<img width="4386" height="2842" alt="image" src="https://github.com/user-attachments/assets/ffd7baf4-e60d-44d0-8863-fe6cfe214e2c" />

### 피드/피드 댓글

- TODO

---

### 주요 기능

- TODO