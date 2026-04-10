package com.zs.practice1.controller;

import com.zs.practice1.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Hello world controller.
 */
@RestController
@RequestMapping("/api")
public class HelloWorldController {
    private static final Logger log = LoggerUtil.getLogger(HelloWorldController.class);

    /**
     * Hello string.
     *
     * @return the string
     */
    @GetMapping("/hello")
    public String hello() {
        log.info("HelloWorld API was called");
        return "Hello World! The Spring Boot application is up and running.";
    }
}
