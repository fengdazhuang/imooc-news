package com.fzz.admin.reporsitory;

import com.fzz.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FriendLinkReporsitory extends MongoRepository<FriendLinkMO,String> {

}
