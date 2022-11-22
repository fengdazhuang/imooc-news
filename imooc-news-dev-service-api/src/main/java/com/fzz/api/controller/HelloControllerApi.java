package com.fzz.api.controller;

import org.springframework.web.bind.annotation.GetMapping;

public interface HelloControllerApi {

    @GetMapping("/hello")
    public String hello();
}
