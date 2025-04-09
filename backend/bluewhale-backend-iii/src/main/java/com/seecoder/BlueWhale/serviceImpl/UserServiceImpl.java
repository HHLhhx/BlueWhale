package com.seecoder.BlueWhale.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.util.UserHolder;
import com.seecoder.BlueWhale.vo.SafeUserVO;
import com.seecoder.BlueWhale.vo.UserVO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.seecoder.BlueWhale.util.RedisConstants.*;

/**
 * @Author: GaoZhaolong
 * @Date: 14:46 2023/11/26
 * <p>
 * 注册登录功能实现
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

    @Resource
    RBloomFilter<String> registerBloomFilter;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Boolean register(UserVO userVO) {
        // 检查是否已经存在
        if (hasRegistered(userVO.getPhone())) {
            throw BlueWhaleException.phoneAlreadyExists();
        }
        userVO.setPassword(securityUtil.encodePassword(userVO.getPassword()));

        // 注册用户保存到数据库
        User newUser = userVO.toPO();
        newUser.setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()));
        userRepository.save(newUser);

        // 将新注册的用户加入布隆过滤器
        registerBloomFilter.add(newUser.getPhone());

        logger.info("{}[{}] registered successfully", userVO.getName(), userVO.getRole());
        return true;
    }

    private boolean hasRegistered(String phone) {
        // 检查布隆过滤器
        if (registerBloomFilter.contains(phone)) {
            return true;
        }

        // 如果不在布隆过滤器中，查询数据库
        User user = userRepository.findByPhone(phone);
        if (user != null) {
            // 如果在数据库中存在，则加入布隆过滤器
            registerBloomFilter.add(phone);
            return true;
        }
        return false;
    }

    @Override
    public String login(String phone, String password) {
        // 检查布隆过滤器（避免缓存穿透）
        if (!registerBloomFilter.contains(phone)) {
            throw BlueWhaleException.phoneOrPasswordError();
        }

        // 检查数据库
        User user = userRepository.findByPhoneAndPassword(phone, securityUtil.encodePassword(password));
        if (user == null) {
            throw BlueWhaleException.phoneOrPasswordError();
        }

        // 生成token和用户信息
        String token = tokenUtil.getToken(user);
        UserVO userVO = user.toVO();
        Map<String, Object> userMap = BeanUtil.beanToMap(
                userVO, new HashMap<>(),
                CopyOptions.create()
                        .setFieldValueEditor((fieldName, fieldValue) -> {
                            if (fieldValue == null) return null;
                            if (fieldValue instanceof Date) {
                                return ((Date) fieldValue).getTime();
                            }
                            return fieldValue.toString();
                        })
        );

        // 将用户信息存入Redis
        String key = LOGIN_TOKEN_KEY + token;
        redisTemplate.opsForHash().putAll(key, userMap);
        redisTemplate.expire(key, LOGIN_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        // 正常登出会删除redis，但是登出功能被前端处理了......

        logger.info("{}[{}] login", user.getName(), user.getRole());
        return token;
    }

    @Override
    public UserVO getInformation() {
        // 从ThreadLocal中获取用户信息
        User user = UserHolder.getUser().toPO();
        if (user.getRole() == RoleEnum.STAFF) {
            // 如果是员工，则需要查询商店名称
            return wrapWithStoreName(user.toVO());
        }
        return user.toVO();
    }

    @Override
    public Boolean updateInformation(UserVO userVO) {
        // 更新用户信息
        User user = UserHolder.getUser().toPO();
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

        // 更新当前线程中的用户信息
        UserHolder.saveUser(user.toVO());

        // 更新Redis中的用户信息
        String token = securityUtil.getToken();
        redisTemplate.opsForHash().put(LOGIN_TOKEN_KEY + token, "password", user.getPassword());
        redisTemplate.opsForHash().put(LOGIN_TOKEN_KEY + token, "name", user.getName());
        redisTemplate.opsForHash().put(LOGIN_TOKEN_KEY + token, "address", user.getAddress());
        redisTemplate.expire(LOGIN_TOKEN_KEY + token, LOGIN_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        logger.info("{} information updated", user.getId());
        return true;
    }

    // 前端没用
    @Override
    public SafeUserVO getUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw BlueWhaleException.userNotExist();
        }
        return user.toVO().toSafeVO();
    }

    private UserVO wrapWithStoreName(UserVO userVO) {
        // 如果是员工，则需要查询商店名称
        Integer storeId = userVO.getStoreId();
        if (storeId == null) {
            return userVO;
        }
        Store store = storeRepository.findById(storeId).get();
        userVO.setStoreName(store.getName());

        // 更新当前线程中的用户信息
        UserHolder.saveUser(userVO);

        // 更新Redis中的用户信息
        String token = securityUtil.getToken();
        redisTemplate.opsForHash().put(LOGIN_TOKEN_KEY + token, "storeName", store.getName());
        redisTemplate.expire(LOGIN_TOKEN_KEY + token, LOGIN_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        return userVO;
    }

}
