package com.lion.be.acceptance;

import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.user.UserSteps.원준_회원가입;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.lion.be.acceptance.util.DatabaseCleanup;
import com.lion.be.acceptance.util.SqlFileExecutor;
import com.lion.be.acceptance.util.TableCleanup;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public abstract class AcceptanceTest {

    private static final RabbitMQContainer rabbitmq;

    static {
        // MySQLContainer 시작
        MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));
        mysql.start();
        System.setProperty("DB_URL", mysql.getJdbcUrl());
        System.setProperty("DB_USERNAME", mysql.getUsername());
        System.setProperty("DB_PASSWORD", mysql.getPassword());

        // Redis 시작
        GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2")).withExposedPorts(6379);
        redis.start();
        System.setProperty("REDIS_HOST", redis.getHost());
        System.setProperty("REDIS_PORT", redis.getMappedPort(6379).toString());

        // MongoDB 시작
        MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));
        mongo.start();
        System.setProperty("MONGO_URI", mongo.getReplicaSetUrl());

        // RabbitMQ 시작 (STOMP 플러그인 활성화)
        rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
                .withPluginsEnabled("rabbitmq_stomp") // STOMP 플러그인 활성화
                .withExposedPorts(5672, 15672, 61613); // AMQP, Management, STOMP 포트 노출
        rabbitmq.start();
        System.setProperty("RABBITMQ_HOST", rabbitmq.getHost());
        System.setProperty("RABBITMQ_PORT", String.valueOf(rabbitmq.getAmqpPort()));
        System.setProperty("RABBITMQ_STOMP_PORT", String.valueOf(rabbitmq.getMappedPort(61613))); // STOMP 포트 설정
        System.setProperty("RABBITMQ_USERNAME", rabbitmq.getAdminUsername());
        System.setProperty("RABBITMQ_PASSWORD", rabbitmq.getAdminPassword());
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // static 블록에서 System.setProperty로 설정한 값을 읽어와서 주입
        registry.add("spring.datasource.url", () -> System.getProperty("DB_URL"));
        registry.add("spring.datasource.username", () -> System.getProperty("DB_USERNAME"));
        registry.add("spring.datasource.password", () -> System.getProperty("DB_PASSWORD"));

        registry.add("spring.data.redis.host", () -> System.getProperty("REDIS_HOST"));
        registry.add("spring.data.redis.port", () -> System.getProperty("REDIS_PORT"));

        registry.add("spring.data.mongodb.uri", () -> System.getProperty("MONGO_URI"));

        // RabbitMQ 설정
        registry.add("spring.rabbitmq.host", () -> System.getProperty("RABBITMQ_HOST"));
        registry.add("spring.rabbitmq.port", () -> System.getProperty("RABBITMQ_PORT"));
        registry.add("spring.rabbitmq.username", () -> System.getProperty("RABBITMQ_USERNAME"));
        registry.add("spring.rabbitmq.password", () -> System.getProperty("RABBITMQ_PASSWORD"));

        // STOMP 설정 추가
        registry.add("spring.messaging.stomp.broker-relay.host", () -> System.getProperty("RABBITMQ_HOST"));
        registry.add("spring.messaging.stomp.broker-relay.port", () -> System.getProperty("RABBITMQ_STOMP_PORT"));
        registry.add("spring.messaging.stomp.broker-relay.system-login", () -> System.getProperty("RABBITMQ_USERNAME"));
        registry.add("spring.messaging.stomp.broker-relay.system-passcode", () -> System.getProperty("RABBITMQ_PASSWORD"));
        registry.add("spring.messaging.stomp.broker-relay.client-login", () -> System.getProperty("RABBITMQ_USERNAME"));
        registry.add("spring.messaging.stomp.broker-relay.client-passcode", () -> System.getProperty("RABBITMQ_PASSWORD"));
    }

    @LocalServerPort
    public int port;

    public String 회원_원준_액세스토큰;

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @Autowired
    protected TableCleanup tableCleanup;

    @Autowired
    protected SqlFileExecutor sqlFileExecutor;

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
        데이터베이스를_초기화한다();
        initAccessToken();
        this.spec = new RequestSpecBuilder().addFilter(
                RestAssuredRestDocumentation.documentationConfiguration(provider)).build();
    }

    private void initAccessToken() {
        회원_원준_액세스토큰 = 원준_액세스토큰_요청();
    }

    private static String 원준_액세스토큰_요청() {
        원준_회원가입();
        return 원준이_로그인한다(new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

}