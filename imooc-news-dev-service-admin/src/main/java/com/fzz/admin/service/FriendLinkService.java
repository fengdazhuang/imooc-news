package com.fzz.admin.service;


import com.fzz.mo.FriendLinkMO;

import java.util.List;

public interface FriendLinkService  {
    List<FriendLinkMO> getFriendLinkList();

    void saveOrUpdateFriendLink(FriendLinkMO friendLink);

    void deleteFriendLink(String linkId);
}
