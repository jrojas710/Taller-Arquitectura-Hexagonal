package co.edu.unab.stagepass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StagePassApplication {

    public static void main(String[] args) {
        SpringApplication.run(StagePassApplication.class, args);
    }
}
