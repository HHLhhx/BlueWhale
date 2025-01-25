package com.seecoder.BlueWhale.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.CouponSet;
import com.seecoder.BlueWhale.repository.CouponSetRepository;
import com.seecoder.BlueWhale.vo.CouponSetVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import com.seecoder.BlueWhale.vo.StoreVO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

public class CouponTestUtil {

    public static CouponSetVO createLocalCouponSetOk(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                                                     StoreVO store, CouponSetRepository couponSetRepository) throws Exception {
        CouponSetVO couponSetVO = new CouponSetVO();
        couponSetVO.setCouponType(CouponTypeEnum.SPECIAL);
        couponSetVO.setTotalNum(500);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2025);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        couponSetVO.setExpireTime(date);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/coupons")
                        .contentType("application/json")
                        .header("token", userToken)
                        .content(objectMapper.writeValueAsString(couponSetVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")));
        List<CouponSet> expectedCouponSetList = couponSetRepository.findAll();
        Assertions.assertNotNull(expectedCouponSetList);
        CouponSetVO expectedCouponSetVO = expectedCouponSetList.get(expectedCouponSetList.size() - 1).toVO();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coupons/" + expectedCouponSetVO.getId() + "/get")
                        .contentType("application/json")
                        .header("token", userToken)
                        .content(objectMapper.writeValueAsString(couponSetVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")));
        Assertions.assertEquals(expectedCouponSetVO.getExpireTime(), couponSetVO.getExpireTime());
        return expectedCouponSetVO;
    }

    public static CouponVO getCoupon(MockMvc mockMvc, ObjectMapper objectMapper, String userToken, int couponSetId)
            throws Exception {
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/coupons/" + couponSetId + "/acquire")
                        .contentType("application/json")
                        .header("token", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")))
                .andReturn();
        JsonNode resultNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
        Coupon coupon = objectMapper.treeToValue(resultNode, Coupon.class);
        Assertions.assertEquals(coupon.getSetId(), couponSetId);
        return coupon.toVO();
    }

}
