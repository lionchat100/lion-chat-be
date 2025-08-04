package com.lion.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LionChatBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LionChatBeApplication.class, args);
    }

}
