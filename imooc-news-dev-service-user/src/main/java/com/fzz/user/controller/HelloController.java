package com.fzz.user.controller;

import com.fzz.api.controller.HelloControllerApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {


    public String hello(){
        return "hello";
    }
}
