package cn.yiming1234.gitstarcenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.yiming1234.gitstarcenter.mapper")
public class GitStarCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GitStarCenterApplication.class, args);
    }
}
