package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.po.CouponSet;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.serviceImpl.strategy.FullReductionCouponCalculateStrategy;
import com.seecoder.BlueWhale.serviceImpl.strategy.SpecialCouponCalculateStrategy;
import com.seecoder.BlueWhale.util.TokenUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlueWhaleApplicationTests {

    @Autowired
    TokenUtil tokenUtil;

    @Test
    void contextLoads() {
        User user = new User();
        user.setId(1);
        user.setPassword("123456");
        System.out.println(tokenUtil.getToken(user));
    }

}
