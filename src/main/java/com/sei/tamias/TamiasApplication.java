package com.sei.tamias;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author gaoxiaodong
 */
@SpringBootApplication
@MapperScan("com.sei.tamias.db.mapper")
@EnableCaching
public class TamiasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TamiasApplication.class, args);
    }

}
