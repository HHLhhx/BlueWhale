package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.vo.SafeUserVO;
import com.seecoder.BlueWhale.vo.UserVO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.hibernate.annotations.FetchProfile.FetchOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: GaoZhaolong
 * @Date: 14:46 2023/11/26
 *
 *        注册登录功能实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    StoreRepository storeRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Boolean register(UserVO userVO) {
        User user = userRepository.findByPhone(userVO.getPhone());
        if (user != null) {
            throw BlueWhaleException.phoneAlreadyExists();
        }
        userVO.setPassword(securityUtil.encodePassword(userVO.getPassword()));
        User newUser = userVO.toPO();
        newUser.setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()));
        userRepository.save(newUser);
        logger.info(String.format("%s[%s] registered successfully", userVO.getName(), userVO.getRole()));
        return true;
    }

    @Override
    public String login(String phone, String password) {
        User user = userRepository.findByPhoneAndPassword(phone, securityUtil.encodePassword(password));
        if (user == null) {
            throw BlueWhaleException.phoneOrPasswordError();
        }
        logger.info(String.format("%s[%s] login", user.getName(), user.getRole()));
        return tokenUtil.getToken(user);
    }

    @Override
    public UserVO getInformation() {
        User user = securityUtil.getCurrentUser();
        if (user.getRole() == RoleEnum.STAFF) {
            return wrapWithStoreName(user.toVO());
        }
        return user.toVO();
    }

    @Override
    public Boolean updateInformation(UserVO userVO) {
        User user = securityUtil.getCurrentUser();
        if (userVO.getPassword() != null) {
            user.setPassword(securityUtil.encodePassword(userVO.getPassword()));
        }
        if (userVO.getName() != null) {
            user.setName(userVO.getName());
        }
        if (userVO.getAddress() != null) {
            user.setAddress(userVO.getAddress());
        }
        userRepository.save(user);
        logger.info(String.format("%s information updated", user.getId()));
        return true;
    }

    @Override
    public SafeUserVO getUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null){
            throw BlueWhaleException.userNotExist();
        }
        return user.toVO().toSafeVO();
    }

    private UserVO wrapWithStoreName(UserVO userVO) {
        Integer storeId = userVO.getStoreId();
        if (storeId == null) {
            return userVO;
        }
        Store store = storeRepository.findById(storeId).get();
        userVO.setStoreName(store.getName());
        return userVO;
    }
}
