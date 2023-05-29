package cn.mrcsh.mcquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class McQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(McQueryApplication.class, args);
    }

}
