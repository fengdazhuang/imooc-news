package com.fzz.admin.service.impl;

import com.fzz.admin.reporsitory.FriendLinkReporsitory;
import com.fzz.admin.service.FriendLinkService;
import com.fzz.mo.FriendLinkMO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkReporsitory friendLinkReporsitory;

    @Override
    public List<FriendLinkMO> getFriendLinkList() {
        List<FriendLinkMO> list = friendLinkReporsitory.findAll();
        return list;
    }

    @Override
    public void saveOrUpdateFriendLink(FriendLinkMO friendLink) {
        if(StringUtils.isBlank(friendLink.getId())){
            friendLink.setUpdateTime(new Date());
            friendLinkReporsitory.save(friendLink);
        }/*else{
            friendLinkReporsitory.
        }*/
    }

    @Override
    public void deleteFriendLink(String linkId) {
        friendLinkReporsitory.deleteById(linkId);
    }
}
