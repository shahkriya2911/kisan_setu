package com.project.kisan_setu;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableScheduling
@SpringBootApplication
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class KisanSetuApplication {

    public static void main(String[] args) {
        SpringApplication.run(KisanSetuApplication.class, args);
    }
}