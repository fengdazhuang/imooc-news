package com.fzz.admin.service.impl;

import com.fzz.admin.reporsitory.FriendLinkReporsitory;
import com.fzz.admin.service.FriendLinkService;
import com.fzz.mo.FriendLinkMo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkReporsitory friendLinkReporsitory;

    @Override
    public List<FriendLinkMo> getFriendLinkList() {
        List<FriendLinkMo> list = friendLinkReporsitory.findAll();
        return list;
    }

    @Override
    public void saveOrUpdateFriendLink(FriendLinkMo friendLink) {
        if(friendLink.getId()==null){
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
