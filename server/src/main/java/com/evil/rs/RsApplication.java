package com.evil.rs;

import com.evil.rs.socket.UDPServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.evil.rs.mapper")
@ServletComponentScan
public class RsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsApplication.class, args);
        new Thread(new UDPServer()).start();
    }

}
