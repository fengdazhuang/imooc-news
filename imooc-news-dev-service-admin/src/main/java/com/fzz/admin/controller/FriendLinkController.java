package com.fzz.admin.controller;

import com.fzz.admin.service.FriendLinkService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.admin.FriendLinkControllerApi;
import com.fzz.bo.AddFriendLinkBO;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.mo.FriendLinkMO;
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
        List<FriendLinkMO> list = friendLinkService.getFriendLinkList();
        return GraceJSONResult.ok(list);
    }

    @Override
    public GraceJSONResult saveOrUpdateFriendLink(AddFriendLinkBO addFriendLinkBo, BindingResult result) {
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        FriendLinkMO friendLink=new FriendLinkMO();
        BeanUtils.copyProperties(addFriendLinkBo,friendLink);

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
        List<FriendLinkMO> list = friendLinkService.getFriendLinkList();
        return GraceJSONResult.ok(list);
    }
}
