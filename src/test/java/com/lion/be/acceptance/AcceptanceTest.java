package com.lion.be.acceptance;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.user.UserSteps.비회원_회원가입;
import static com.lion.be.acceptance.user.UserSteps.원준_회원가입;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import com.lion.be.acceptance.util.DatabaseCleanup;
import com.lion.be.acceptance.util.MongoCleanup;
import com.lion.be.acceptance.util.SqlFileExecutor;
import com.lion.be.acceptance.util.TableCleanup;
import com.lion.be.global.service.RateLimitingService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
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
    private static final RabbitMQContainer rabbitmq;
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

        // RabbitMQ 시작 (STOMP 플러그인 활성화)
        rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
                .withPluginsEnabled("rabbitmq_stomp")
                .withExposedPorts(5672, 15672, 61613);
        rabbitmq.start();
        System.setProperty("RABBITMQ_HOST", rabbitmq.getHost());
        System.setProperty("RABBITMQ_PORT", String.valueOf(rabbitmq.getAmqpPort()));
        System.setProperty("RABBITMQ_STOMP_PORT", String.valueOf(rabbitmq.getMappedPort(61613)));
        System.setProperty("RABBITMQ_USERNAME", rabbitmq.getAdminUsername());
        System.setProperty("RABBITMQ_PASSWORD", rabbitmq.getAdminPassword());

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

        // RabbitMQ 설정
        registry.add("spring.rabbitmq.host", () -> System.getProperty("RABBITMQ_HOST"));
        registry.add("spring.rabbitmq.port", () -> System.getProperty("RABBITMQ_PORT"));
        registry.add("spring.rabbitmq.username", () -> System.getProperty("RABBITMQ_USERNAME"));
        registry.add("spring.rabbitmq.password", () -> System.getProperty("RABBITMQ_PASSWORD"));

        // STOMP 설정
        registry.add("spring.messaging.stomp.broker-relay.host", () -> System.getProperty("RABBITMQ_HOST"));
        registry.add("spring.messaging.stomp.broker-relay.port", () -> System.getProperty("RABBITMQ_STOMP_PORT"));
        registry.add("spring.messaging.stomp.broker-relay.system-login", () -> System.getProperty("RABBITMQ_USERNAME"));
        registry.add("spring.messaging.stomp.broker-relay.system-passcode",
                () -> System.getProperty("RABBITMQ_PASSWORD"));
        registry.add("spring.messaging.stomp.broker-relay.client-login", () -> System.getProperty("RABBITMQ_USERNAME"));
        registry.add("spring.messaging.stomp.broker-relay.client-passcode",
                () -> System.getProperty("RABBITMQ_PASSWORD"));

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

        // 여기에 초기화 코드 추가
        rateLimitingService.clearBuckets();

        initAccessToken();
        this.spec = new RequestSpecBuilder().addFilter(
                RestAssuredRestDocumentation.documentationConfiguration(provider)).build();
    }

    private void initAccessToken() {
        회원_원준_액세스토큰 = 원준_액세스토큰_요청();
        비회원_엑세스토큰 = 비회원_액세스토큰_요청();
    }

    private static String 원준_액세스토큰_요청() {
        원준_회원가입();
        return 원준이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

    private static String 비회원_액세스토큰_요청() {
        비회원_회원가입();
        return 비회원이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

}
