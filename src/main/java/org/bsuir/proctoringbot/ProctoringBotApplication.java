package org.bsuir.proctoringbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProctoringBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProctoringBotApplication.class, args);
    }

}
