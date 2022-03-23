package com.ecom.throttling.controller;

import com.ecom.throttling.service.SomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/throttling")
public class ThrottlingController {

    private final SomeService someService;

    public ThrottlingController(SomeService someService) {
        this.someService = someService;
    }

    @GetMapping("")
    public void get() {
        someService.someMethod();
    }
}
