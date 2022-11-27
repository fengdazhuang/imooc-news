package com.fzz.api.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/appUser")
public interface AppUserControllerApi {

    @PostMapping("/queryAll")
    public Object queryAll(@RequestParam String nickname,
                           @RequestParam Integer status,
                           @RequestParam String startDate,
                           @RequestParam String endDate,
                           @RequestParam Integer page,
                           @RequestParam Integer pageSize);

    @PostMapping("/userDetail")
    public Object getUserDetail(@RequestParam Long userId);

    @PostMapping("/freezeUserOrNot")
    public Object freezeUserOrNot(@RequestParam Long userId,@RequestParam Integer doStatus);
}
