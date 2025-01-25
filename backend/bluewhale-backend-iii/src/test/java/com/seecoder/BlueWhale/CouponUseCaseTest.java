package com.seecoder.BlueWhale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.po.CouponSet;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.CouponSetRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.util.CouponTestUtil;
import com.seecoder.BlueWhale.util.StoreTestUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.util.UserTestUtil;
import com.seecoder.BlueWhale.vo.CouponSetVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import com.seecoder.BlueWhale.vo.UserVO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CouponUseCaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponSetRepository couponSetRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TokenUtil tokenUtil;

    private static final Logger logger = LoggerFactory.getLogger(CouponUseCaseTest.class);

    @Test
    @Transactional(rollbackOn = Exception.class)
    void create() throws Exception {
        String managerToken = UserTestUtil.createManagerOk(mockMvc, objectMapper, tokenUtil, userRepository);
        StoreVO storeVO = StoreTestUtil.createStoreOk(mockMvc, objectMapper, managerToken, storeRepository, "test");
        String staffToken = UserTestUtil.createStaffOk(mockMvc, objectMapper, tokenUtil, userRepository, storeVO.getId());
        CouponSetVO couponSetVO = CouponTestUtil.createLocalCouponSetOk(mockMvc, objectMapper, staffToken, storeVO,
                couponSetRepository);
    }

    @Test
    @Transactional(rollbackOn = Exception.class)
    void acquire() throws Exception {
        Map<String, String> users = new HashMap<String, String>();
        for (int i = 0; i < 1000; i++) {
            String testUid = String.format("%03d", i);
            String phoneStr = "13727883" + testUid;
            // register and put tokens into users map
            users.put(phoneStr, UserTestUtil.createCustomerOk(mockMvc, objectMapper, tokenUtil, userRepository, phoneStr));
        }
        // create coupon set
        String userToken = UserTestUtil.createManagerOk(mockMvc, objectMapper, tokenUtil, userRepository);
        StoreVO storeVO = StoreTestUtil.createStoreOk(mockMvc, objectMapper, userToken, storeRepository, "test");
        String staffToken = UserTestUtil.createStaffOk(mockMvc, objectMapper, tokenUtil, userRepository, storeVO.getId());
        CouponSetVO couponSetVO = CouponTestUtil.createLocalCouponSetOk(mockMvc, objectMapper, staffToken, storeVO,
                couponSetRepository); // 500 张优惠券

        // get coupon in different thread
        AtomicInteger successTime = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 创建并提交任务
        for (int i = 0; i < 10; i++) {
            final int threadId = i;

            executor.submit(() -> {
                try {
                    logger.debug("thread " + threadId + "start");
                    for (int j = threadId * 100; j < (threadId + 1) * 100; j++) {
                        String phoneStr = "13727883" + String.format("%03d", j);
                        String token = users.get(phoneStr);
                        Assertions.assertNotNull(token);
                        CouponVO coupon = CouponTestUtil.getCoupon(mockMvc, objectMapper, token,
                                couponSetVO.getId());
                        successTime.incrementAndGet();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            });
        }
        // 确保所有任务完成之后再继续执行其他代码
        executor.shutdown();
        try {
            // 等待所有任务完成，最多等待300秒
            if (!executor.awaitTermination(300, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                // 再次等待所有任务终止
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("线程池未能正确关闭！");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // check number of couponSet
        CouponSet doneCouponSet = couponSetRepository.findById(couponSetVO.getId()).orElse(null);
        Assertions.assertNotNull(doneCouponSet);
        Assertions.assertEquals(doneCouponSet.getSentNum(),
                doneCouponSet.getTotalNum());
        Assertions.assertEquals(doneCouponSet.getSentNum().intValue(),
                successTime.get());
    }

}
