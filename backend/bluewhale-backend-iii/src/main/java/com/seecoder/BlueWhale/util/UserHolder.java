package com.seecoder.BlueWhale.util;

import com.seecoder.BlueWhale.vo.UserVO;

public class UserHolder {
    private static final ThreadLocal<UserVO> currentUser = new ThreadLocal<>();

    public static void saveUser(UserVO user) {
        currentUser.set(user);
    }

    public static UserVO getUser(){
        return currentUser.get();
    }

    public static void removeUser(){
        currentUser.remove();
    }
}
