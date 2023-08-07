package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class InfoController {
    @GetMapping("api")
    public String projectInfo() {
        String a = "hi";

        return a;
    }
}
