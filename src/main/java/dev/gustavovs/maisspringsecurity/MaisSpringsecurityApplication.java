package dev.gustavovs.maisspringsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaisSpringsecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaisSpringsecurityApplication.class, args);
    }

}
