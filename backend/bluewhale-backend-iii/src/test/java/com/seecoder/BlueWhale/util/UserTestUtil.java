package com.seecoder.BlueWhale.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.seecoder.BlueWhale.po.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.vo.UserVO;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.is;

public class UserTestUtil {
    private static final Logger logger = LoggerFactory.getLogger(UserTestUtil.class);

    public static String createManagerOk(MockMvc mockMvc, ObjectMapper objectMapper, TokenUtil tokenUtil,
                                         UserRepository userRepository) throws Exception {
        // Register
        UserVO userVO = new UserVO();
        userVO.setPassword("123456");
        userVO.setAddress("NJU Manager");
        userVO.setName("Manager");
        userVO.setPhone("16766666665");
        userVO.setRole(RoleEnum.MANAGER);
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")));

        // Login
        String expectedToken = tokenUtil.getToken(userRepository.findByPhone("16766666665"));
        assertEquals(tokenUtil.getUser(expectedToken).getPhone(), "16766666665");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType("application/json")
                        .param("phone", "16766666665")
                        .param("password", "123456")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")))
                .andReturn();
        // .andExpect(jsonPath("$.result", is(expectedToken)));
        JsonNode resultNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
        String token = objectMapper.treeToValue(resultNode, String.class);
        assertEquals(tokenUtil.getUser(token).getPhone(), "16766666665");
        return expectedToken;
    }

    public static String createStaffOk(MockMvc mockMvc, ObjectMapper objectMapper, TokenUtil tokenUtil,
                                       UserRepository userRepository, int storeId) throws Exception {
        // Register
        UserVO userVO = new UserVO();
        userVO.setPassword("123456");
        userVO.setAddress("NJU Staff");
        userVO.setName("Staff");
        userVO.setPhone("16766456666");
        userVO.setRole(RoleEnum.STAFF);
        userVO.setStoreId(storeId);
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")));

        // Login
        String expectedToken = tokenUtil.getToken(userRepository.findByPhone("16766456666"));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType("application/json")
                        .param("phone", "16766456666")
                        .param("password", "123456")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000"))).andReturn();
        JsonNode resultNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
        String token = objectMapper.treeToValue(resultNode, String.class);
        assertEquals(tokenUtil.getUser(token).getPhone(), "16766456666");
        return expectedToken;
    }

    public static String createCustomerOk(MockMvc mockMvc, ObjectMapper objectMapper, TokenUtil tokenUtil,
                                          UserRepository userRepository, String phone) throws Exception {
        // Register
        UserVO userVO = new UserVO();
        userVO.setPassword("123456");
        userVO.setAddress("NJU customer");
        userVO.setName("customer");
        userVO.setPhone(phone);
        userVO.setRole(RoleEnum.CUSTOMER);
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000")));

        // Login
        String expectedToken = tokenUtil.getToken(userRepository.findByPhone(phone));
        User curUser = userRepository.findByPhone(phone);
        assertNotNull(curUser);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType("application/json")
                        .param("phone", phone)
                        .param("password", "123456")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("000"))).andReturn();
        JsonNode resultNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
        String getToken = objectMapper.treeToValue(resultNode, String.class);
        assertEquals(tokenUtil.getUser(getToken).getPhone(), phone);
        return expectedToken;
    }

    public static String createCustomerOk(MockMvc mockMvc, ObjectMapper objectMapper, TokenUtil tokenUtil,
                                          UserRepository userRepository) throws Exception {
        return createCustomerOk(mockMvc, objectMapper, tokenUtil, userRepository, "16766456706");
    }

}
