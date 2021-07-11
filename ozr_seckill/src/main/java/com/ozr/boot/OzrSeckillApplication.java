package com.ozr.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ozr.boot.dao")
public class OzrSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(OzrSeckillApplication.class, args);
    }

}
