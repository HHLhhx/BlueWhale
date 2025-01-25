package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.vo.SafeUserVO;
import com.seecoder.BlueWhale.vo.UserVO;

public interface UserService {
    Boolean register(UserVO userVO);

    String login(String phone,String password);

    UserVO getInformation();

    Boolean updateInformation(UserVO userVO);

    SafeUserVO getUser(Integer id);
}
