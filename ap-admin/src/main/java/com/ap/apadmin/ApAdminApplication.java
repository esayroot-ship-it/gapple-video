package com.ap.apadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ap.apadmin", "com.ap.apservice", "com.ap.apcommon"})
@MapperScan(basePackages = {"com.ap.apadmin.dao","com.ap.apservice.dao","com.ap.apcommon.dao"})
public class ApAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApAdminApplication.class, args);
    }

}
