package com.fzz.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
@EnableEurekaClient
@ComponentScan("com.fzz")
public class EsSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsSearchApplication.class,args);
    }
}
