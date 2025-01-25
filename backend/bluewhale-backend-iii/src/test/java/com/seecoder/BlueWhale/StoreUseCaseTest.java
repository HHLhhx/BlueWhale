package com.seecoder.BlueWhale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.util.ProductTestUtil;
import com.seecoder.BlueWhale.util.StoreTestUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.util.UserTestUtil;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import com.seecoder.BlueWhale.vo.UserVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StoreUseCaseTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private TokenUtil tokenUtil;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private StoreRepository storeRepository;

        @Autowired
        private ProductRepository productRepository;

        @Test
        @Transactional(rollbackOn = Exception.class)
        void create() throws Exception {
                // Remove existing user
                userRepository.deleteByPhone("16766666666");

                // Register
                UserVO userVO = new UserVO();
                userVO.setPassword("123456");
                userVO.setAddress("NJU Manager");
                userVO.setName("Manager");
                userVO.setPhone("16766666666");
                userVO.setRole(RoleEnum.MANAGER);
                mockMvc.perform(post("/api/users/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(userVO)))
                                .andExpect(status().isOk());

                // Login
                String expectedToken = tokenUtil.getToken(userRepository.findByPhone("16766666666"));
                mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                                .contentType("application/json")
                                .param("phone", "16766666666")
                                .param("password", "123456")).andExpect(status().isOk())
                                .andExpect(jsonPath("$.result", is(expectedToken)));

                // Create store
                StoreVO storeVO = new StoreVO();
                storeVO.setName("Test Store");
                storeVO.setLocation("Test Location");
                storeVO.setLogoUrl("https://th.bing.com/th/id/OIP.Eib_F-NYXZeCsu355BnugwHaHa?rs=1&pid=ImgDetMain");
                mockMvc.perform(MockMvcRequestBuilders.post("/api/stores")
                                .contentType("application/json")
                                .header("token", expectedToken)
                                .content(objectMapper.writeValueAsString(storeVO))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.result", is(true)));

                Store storePO = storeRepository.findByName(storeVO.getName());
                Assertions.assertEquals(storePO.getLocation(), storeVO.getLocation());
        }

        @Test
        @Transactional(rollbackOn = Exception.class)
        void search() throws Exception {
                String managerToken = UserTestUtil.createManagerOk(mockMvc, objectMapper, tokenUtil, userRepository);
                StoreVO storeVO = StoreTestUtil.createStoreOk(mockMvc, objectMapper, managerToken, storeRepository,
                                "test");
                String staffToken = UserTestUtil.createStaffOk(mockMvc, objectMapper, tokenUtil, userRepository,
                                storeVO.getId());

                // 范围测试
                ProductVO product1 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test10",
                                10.23, productRepository);
                ProductVO product2 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test11",
                                20.01, productRepository);
                ProductVO product3 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test12",
                                30.78, productRepository);
                ProductVO product4 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test13",
                                40.01, productRepository);
                ProductVO product5 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test14",
                                50.07, productRepository);
                List<ProductVO> productList = StoreTestUtil.searchProduct(mockMvc, objectMapper, managerToken,
                                storeVO.getId(),
                                "test1", 20.0, 40.0);
                assertNotNull(productList);
                assertTrue(productList.size() == 2);
                ProductTestUtil.assertProductEquals(productList.get(0), product2);
                ProductTestUtil.assertProductEquals(productList.get(1), product3);

                // 模糊搜索测试
                ProductVO product6 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test-pro",
                                10.00, productRepository);
                ProductVO product7 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "test-proro",
                                10.00, productRepository);
                ProductVO product8 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "ro-test",
                                10.00, productRepository);
                ProductVO product9 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                                "r.o-test",
                                10.00, productRepository);
                ProductVO product10 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken,
                                storeVO.getId(),
                                "r.%\"'o-test",
                                10.00, productRepository);
                ProductVO product11 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken,
                                storeVO.getId(),
                                ".%\"'ro-test",
                                10.00, productRepository);
                ProductVO product12 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken,
                                storeVO.getId(),
                                "ro",
                                10.00, productRepository);
                ProductVO product13 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken,
                                storeVO.getId(),
                                "RO",
                                10.00, productRepository);
                List<ProductVO> productList2 = StoreTestUtil.searchProduct(mockMvc, objectMapper, managerToken,
                                storeVO.getId(),
                                "ro", 5.0, 40.0);
                assertTrue(productList2.size() == 5);
                ProductTestUtil.assertProductEquals(productList2.get(0), product6);
                ProductTestUtil.assertProductEquals(productList2.get(1), product7);
                ProductTestUtil.assertProductEquals(productList2.get(2), product8);
                ProductTestUtil.assertProductEquals(productList2.get(3), product11);
                ProductTestUtil.assertProductEquals(productList2.get(4), product12);
        }

}
