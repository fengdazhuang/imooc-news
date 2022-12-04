package com.fzz.api.controller.admin;


import com.fzz.bo.AddFriendLinkBO;
import com.fzz.common.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/friendLinkMng")
public interface FriendLinkControllerApi {

    /**
     * 管理系统展现
     * @return
     */
    @PostMapping("/getFriendLinkList")
    public GraceJSONResult getFriendLinkList();

    @PostMapping("/saveOrUpdateFriendLink")
    public GraceJSONResult saveOrUpdateFriendLink(@RequestBody @Valid AddFriendLinkBO addFriendLinkBo, BindingResult result);

    @PostMapping("/delete")
    public GraceJSONResult deleteFriendLink(@RequestParam String linkId);


    /**
     * 主页展现
     */
    @GetMapping("/portal/list")
    public GraceJSONResult list();
}
