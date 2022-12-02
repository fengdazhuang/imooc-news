package com.fzz.admin.service;


import com.fzz.mo.FriendLinkMo;

import java.util.List;

public interface FriendLinkService  {
    List<FriendLinkMo> getFriendLinkList();

    void saveOrUpdateFriendLink(FriendLinkMo friendLink);

    void deleteFriendLink(String linkId);
}
