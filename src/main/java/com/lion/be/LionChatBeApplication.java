package com.lion.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
public class LionChatBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LionChatBeApplication.class, args);
    }

}
