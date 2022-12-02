package com.fzz.admin.controller;

import com.fzz.admin.service.FriendLinkService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.admin.FriendLinkControllerApi;
import com.fzz.bo.AddFriendLinkBo;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.mo.FriendLinkMo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

    @Autowired
    private FriendLinkService friendLinkService;

    @Override
    public GraceJSONResult getFriendLinkList() {
        List<FriendLinkMo> list = friendLinkService.getFriendLinkList();
        return GraceJSONResult.ok(list);
    }

    @Override
    public GraceJSONResult saveOrUpdateFriendLink(AddFriendLinkBo addFriendLinkBo, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        FriendLinkMo friendLink=new FriendLinkMo();
        BeanUtils.copyProperties(addFriendLinkBo,friendLink);
        friendLink.setUpdateTime(new Date());

        friendLinkService.saveOrUpdateFriendLink(friendLink);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult deleteFriendLink(String linkId) {
        friendLinkService.deleteFriendLink(linkId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult list() {
        List<FriendLinkMo> list = friendLinkService.getFriendLinkList();
        return GraceJSONResult.ok(list);
    }
}
