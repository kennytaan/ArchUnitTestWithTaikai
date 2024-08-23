package com.example.spring_boot;

import org.springframework.stereotype.Controller;
import org.springframework.web.service.annotation.GetExchange;

@Controller
public class HelloController {

    @GetExchange("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}