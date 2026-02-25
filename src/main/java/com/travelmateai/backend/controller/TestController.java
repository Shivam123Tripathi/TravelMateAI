package com.travelmateai.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController tells Spring: "This class handles HTTP requests and returns data (not HTML pages)"
// It combines two annotations: @Controller + @ResponseBody
@RestController

// @RequestMapping sets the BASE URL for all endpoints in this class
// So every endpoint here will start with /api
@RequestMapping("/api")
public class TestController {

    // @GetMapping("/hello") means:
    // "When someone sends a GET request to /api/hello, run this method"
    // GET = used to READ/fetch data (most common type of request)
    @GetMapping("/hello")
    public String hello() {

        // Whatever this method returns gets sent back as the HTTP response
        return "Hello from TravelMate AI Backend!";
    }
}
