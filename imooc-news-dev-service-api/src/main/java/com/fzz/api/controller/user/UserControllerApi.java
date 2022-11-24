package com.fzz.api.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/user")
public interface UserControllerApi {

    @PostMapping("/getAccountInfo")
    public Object getAccountInfo(@RequestParam Long userId);
}
