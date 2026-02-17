package com.sentinel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Sentinel API está rodando 🚀";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}