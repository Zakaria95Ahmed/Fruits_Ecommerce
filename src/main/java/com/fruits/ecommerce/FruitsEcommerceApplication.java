package com.fruits.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@RequiredArgsConstructor
public class FruitsEcommerceApplication {

    //That is the main method function to start up the app
    public static void main(String[] args) {
        SpringApplication.run(FruitsEcommerceApplication.class, args);
    }

}
