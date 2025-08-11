package com.lion.be.acceptance.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@DisplayName("유틸리티/부하 테스트 지원")
public class TokenGenerationAcceptanceTest extends AcceptanceTest {

    private static final int TOTAL_USER_COUNT = 1500;

    @Autowired
    private UserJpaRepository userRepository;

    @Value("${jwt.access-token-expire-time}")
    private long accessTokenExpireTime;

    @Test
    @Disabled // 이 테스트는 매번 실행할 필요가 없으므로, 필요할 때만 수동으로 실행하여 tokens.csv 파일을 생성합니다.
    @DisplayName("부하 테스트를 위한 JWT 토큰 1500개 생성")
    void generate_tokens_for_load_test() throws IOException {
        // Step 0: 현재 적용된 토큰 만료 시간 확인
        System.out.println("\n✅ [INFO] 현재 적용된 Access Token 유효 시간: " + accessTokenExpireTime + " ms\n");
        assertThat(accessTokenExpireTime).withFailMessage("jwt.access-token-expire-time을 최소 1시간(3600000) 이상으로 설정해주세요.")
                .isGreaterThanOrEqualTo(3600000L);

        // Step 1: DB에 부족한 만큼 테스트 사용자 생성
        long currentTotalUsers = userRepository.count();
        if (currentTotalUsers < TOTAL_USER_COUNT) {
            int usersToCreate = TOTAL_USER_COUNT - (int) currentTotalUsers;
            System.out.println("\n✅ [INFO] 부족한 사용자 " + usersToCreate + "명을 생성합니다...\n");
            List<User> newUsers = IntStream.range(0, usersToCreate)
                    .mapToObj(i -> {
                        long userIndex = currentTotalUsers + i + 1;
                        return new User(
                                "Test User " + userIndex,
                                "testuser" + userIndex + "@tokit.co.kr",
                                "https://lion-s3.s3.ap-northeast-2.amazonaws.com/etc/default_image.png",
                                Role.USER
                        );
                    })
                    .toList();
            userRepository.saveAll(newUsers);
        }

        // Step 2: 토큰 생성 API 호출
        GenerateTokensRequest tokenRequest = new GenerateTokensRequest(TOTAL_USER_COUNT);

        List<String> rawTokens = RestAssured.given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(tokenRequest)
                .when()
                .post("/api/test/generate-tokens")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath().getList(".", String.class);

        // Step 3: Null 혹은 비정상 토큰 필터링
        List<String> validTokens = rawTokens.stream()
                .filter(token -> Objects.nonNull(token) && token.startsWith("Bearer "))
                .toList();

        // 생성된 유효한 토큰의 수가 예상과 같은지 확인
        assertThat(validTokens).withFailMessage("API가 null 토큰을 반환했습니다. TestUtilityController를 확인하세요.")
                .hasSize(TOTAL_USER_COUNT);

        // Step 4: tokens.csv 파일 작성
        List<String> csvLines = new ArrayList<>();
        csvLines.add("Authorization"); // CSV 헤더 추가
        csvLines.addAll(validTokens);

        // 프로젝트 루트 경로에 파일 생성
        Files.write(Paths.get("tokens.csv"), csvLines);

        System.out.println("\n✅ [SUCCESS] " + validTokens.size() + "개의 유효한 토큰이 포함된 tokens.csv 파일이 프로젝트 루트에 생성되었습니다.\n");
    }

    // 토큰 생성 요청 DTO
    private record GenerateTokensRequest(int count) {
    }
}
