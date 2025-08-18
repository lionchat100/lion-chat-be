package com.lion.be.acceptance;

import static com.lion.be.acceptance.auth.AuthSteps.*;
import static com.lion.be.acceptance.image.ImageSteps.이미지_리스트를_업로드한다;
import static com.lion.be.acceptance.user.UserSteps.비회원_회원가입;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.user.UserSteps.원준_회원가입;
import static com.lion.be.acceptance.user.UserSteps.토킷_회원가입;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import com.lion.be.acceptance.util.DatabaseCleanup;
import com.lion.be.acceptance.util.MongoCleanup;
import com.lion.be.acceptance.util.SqlFileExecutor;
import com.lion.be.acceptance.util.TableCleanup;
import com.lion.be.acceptance.util.UserFixture;
import com.lion.be.global.service.RateLimitingService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public abstract class AcceptanceTest {

    private static final MySQLContainer<?> mysql;
    private static final GenericContainer<?> redis;
    private static final MongoDBContainer mongo;
    private static final GenericContainer<?> activemq;
    private static final LocalStackContainer localstack;

    public static final String S3_BUCKET_NAME = "test-bucket";

    // static 초기화 블록: 모든 테스트 시작 전 단 한 번만 실행
    static {
        // MySQLContainer 시작
        mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));
        mysql.start();
        System.setProperty("DB_URL", mysql.getJdbcUrl());
        System.setProperty("DB_USERNAME", mysql.getUsername());
        System.setProperty("DB_PASSWORD", mysql.getPassword());

        // Redis 시작
        redis = new GenericContainer<>(DockerImageName.parse("redis:7.2")).withExposedPorts(6379);
        redis.start();
        System.setProperty("REDIS_HOST", redis.getHost());
        System.setProperty("REDIS_PORT", redis.getMappedPort(6379).toString());

        // MongoDB 시작
        mongo = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));
        mongo.start();
        System.setProperty("MONGO_URI", mongo.getReplicaSetUrl());

        /* --- 기존 RabbitMQ 시작 블록 전체를 아래 코드로 교체 --- */
        // ActiveMQ 시작
        // ActiveMQ Classic 이미지를 사용하고, 필요한 포트들을 노출합니다.
        // 61616: OpenWire(JMS), 61613: STOMP, 8161: Web Console
        activemq = new GenericContainer<>(DockerImageName.parse("apache/activemq-classic:5.18.3"))
                .withExposedPorts(61616, 61613, 8161);
        activemq.start();
        // System Property 이름을 ActiveMQ에 맞게 변경
        System.setProperty("ACTIVEMQ_HOST", activemq.getHost());
        System.setProperty("ACTIVEMQ_JMS_PORT", String.valueOf(activemq.getMappedPort(61616)));
        System.setProperty("ACTIVEMQ_STOMP_PORT", String.valueOf(activemq.getMappedPort(61613)));
        // ActiveMQ Classic의 기본 계정 정보
        System.setProperty("ACTIVEMQ_USERNAME", "admin");
        System.setProperty("ACTIVEMQ_PASSWORD", "admin");
        /* --- 교체 완료 --- */

        // LocalStack 시작 (S3 서비스만 활성화)
        localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3"))
                .withServices(S3)
                .withReuse(true);
        localstack.start();

        // 테스트 실행 전에 LocalStack 내부에 S3 버킷 생성
        try {
            localstack.execInContainer("awslocal", "s3api", "create-bucket", "--bucket", S3_BUCKET_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create S3 bucket in LocalStack", e);
        }
    }

    @Autowired
    private RateLimitingService rateLimitingService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // MySQL 설정
        registry.add("spring.datasource.url", () -> System.getProperty("DB_URL"));
        registry.add("spring.datasource.username", () -> System.getProperty("DB_USERNAME"));
        registry.add("spring.datasource.password", () -> System.getProperty("DB_PASSWORD"));

        // Redis 설정
        registry.add("spring.data.redis.host", () -> System.getProperty("REDIS_HOST"));
        registry.add("spring.data.redis.port", () -> System.getProperty("REDIS_PORT"));

        // MongoDB 설정
        registry.add("spring.data.mongodb.uri", () -> System.getProperty("MONGO_URI"));

        /* --- 기존 RabbitMQ 및 STOMP 설정 블록 전체를 아래 코드로 교체 --- */
        // ActiveMQ 설정 (JMS/OpenWire 용)
        // broker-url 형식으로 설정합니다.
        registry.add("spring.activemq.broker-url",
                () -> String.format("tcp://%s:%s",
                        System.getProperty("ACTIVEMQ_HOST"),
                        System.getProperty("ACTIVEMQ_JMS_PORT")));
        registry.add("spring.activemq.user", () -> System.getProperty("ACTIVEMQ_USERNAME"));
        registry.add("spring.activemq.password", () -> System.getProperty("ACTIVEMQ_PASSWORD"));

        // STOMP 설정 (STOMP Relay 용)
        // 프로퍼티 키는 동일하지만, 값을 ActiveMQ 정보로 교체합니다.
        registry.add("spring.messaging.stomp.broker-relay.host", () -> System.getProperty("ACTIVEMQ_HOST"));
        registry.add("spring.messaging.stomp.broker-relay.port", () -> System.getProperty("ACTIVEMQ_STOMP_PORT"));
        registry.add("spring.messaging.stomp.broker-relay.system-login", () -> System.getProperty("ACTIVEMQ_USERNAME"));
        registry.add("spring.messaging.stomp.broker-relay.system-passcode", () -> System.getProperty("ACTIVEMQ_PASSWORD"));
        registry.add("spring.messaging.stomp.broker-relay.client-login", () -> System.getProperty("ACTIVEMQ_USERNAME"));
        registry.add("spring.messaging.stomp.broker-relay.client-passcode", () -> System.getProperty("ACTIVEMQ_PASSWORD"));

        // AWS S3
        registry.add("cloud.aws.s3.bucket", () -> S3_BUCKET_NAME);
        registry.add("cloud.aws.region.static", () -> localstack.getRegion());
        registry.add("cloud.aws.credentials.access-key", () -> localstack.getAccessKey());
        registry.add("cloud.aws.credentials.secret-key", () -> localstack.getSecretKey());
        registry.add("spring.cloud.aws.s3.endpoint", () -> localstack.getEndpointOverride(S3).toString());
    }

    @LocalServerPort
    public int port;

    public String 회원_원준_액세스토큰;
    public String 회원_토킷_액세스토큰;
	public String 어드민_멋사_액세스토큰;
    public String 비회원_엑세스토큰;

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @Autowired
    private MongoCleanup mongoCleanup;

    @Autowired
    protected TableCleanup tableCleanup;

    @Autowired
    protected SqlFileExecutor sqlFileExecutor;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    protected RequestSpecification spec;

    public static void api_문서_타이틀(String documentName, RequestSpecification specification) {
        specification.filter(document(
                documentName,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
        ));
    }

    @PostConstruct
    public void setRestAssuredPort() {
        RestAssured.port = port;
    }

    protected void 데이터베이스를_초기화한다() {
        databaseCleanup.execute();
        sqlFileExecutor.execute("data.sql");
    }

    protected void 테이블을_비운다(String tableName) {
        tableCleanup.setTableName(tableName);
        tableCleanup.execute();
    }

    @BeforeEach
    void setSpec(RestDocumentationContextProvider provider) {
        databaseCleanup.execute();
        mongoCleanup.execute();
        sqlFileExecutor.execute("data.sql");
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        rateLimitingService.clearAllBuckets();

        initAccessToken();
        this.spec = new RequestSpecBuilder().setPort(port).addFilter(
                RestAssuredRestDocumentation.documentationConfiguration(provider)).build();
    }

    private void initAccessToken() {
        회원_원준_액세스토큰 = 원준_액세스토큰_요청();
        회원_토킷_액세스토큰 = 토킷_엑세스토큰_요청();
		어드민_멋사_액세스토큰 = 어드민_멋사_액세스토큰_요청();
        비회원_엑세스토큰 = 비회원_액세스토큰_요청();
    }

    private static String 원준_액세스토큰_요청() {
        원준_회원가입();
        return 원준이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

    private static String 토킷_엑세스토큰_요청() {
        토킷_회원가입();
        return 토킷이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

    private static String 비회원_액세스토큰_요청() {
        비회원_회원가입();
        return 비회원이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

	private static String 어드민_멋사_액세스토큰_요청() {
		return 어드민이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
	}

    /**
     * 토킷 완전 온보딩 (로그인 → 이미지 리스트 업로드 → 온보딩) - 더 효율적!
     */
    protected String 토킷_완전_온보딩() throws IOException {
        var loginResponse = 토킷이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // 이미지 리스트 업로드 (2장)
        ExtractableResponse<Response> imageListResponse = 이미지_리스트를_업로드한다(accessToken, spec);
        List<Long> imageIds = imageListResponse.jsonPath().getList("imageId", Long.class);

        // 온보딩 완료
        온보딩을_완료한다(UserFixture.토킷_온보딩_요청(imageIds), accessToken, spec);

        return accessToken;
    }

    /**
     * 원준 완전 온보딩 (로그인 → 이미지 리스트 업로드 → 온보딩) - 더 효율적!
     */
    protected String 원준_완전_온보딩() throws IOException {
        var loginResponse = 원준이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // 이미지 리스트 업로드 (2장)
        ExtractableResponse<Response> imageListResponse = 이미지_리스트를_업로드한다(accessToken, spec);
        List<Long> imageIds = imageListResponse.jsonPath().getList("imageId", Long.class);

        // 온보딩 완료
        온보딩을_완료한다(UserFixture.회원_멋사_온보딩_요청(imageIds), accessToken, spec);

        return accessToken;
    }

    /**
     * 비회원 완전 온보딩 (로그인 → 이미지 리스트 업로드 → 온보딩) - 더 효율적!
     */
    protected String 비회원_완전_온보딩() throws IOException {
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // 이미지 리스트 업로드 (2장)
        ExtractableResponse<Response> imageListResponse = 이미지_리스트를_업로드한다(accessToken, spec);
        List<Long> imageIds = imageListResponse.jsonPath().getList("imageId", Long.class);

        // 온보딩 완료
        온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(imageIds), accessToken, spec);

        return accessToken;
    }

	protected static final Map<String, Object> 토킷_사용자의_로그인_정보 = Map.of(
		"email", "토킷_이메일@example.com",
		"password", "토킷_비밀번호"
	);
}
