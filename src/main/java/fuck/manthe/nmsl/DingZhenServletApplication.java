package fuck.manthe.nmsl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DingZhenServletApplication {

    public static void main(String[] args) {
        SpringApplication.run(DingZhenServletApplication.class, args);
    }

}
