package com.seecoder.BlueWhale.util;

import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.hamcrest.Matchers.is;

public class StoreTestUtil {
        public static StoreVO createStoreOk(MockMvc mockMvc, ObjectMapper objectMapper, String token,
                        StoreRepository storeRepository, String name) throws Exception {
                // Create store
                StoreVO storeVO = new StoreVO();
                storeVO.setName(name);
                storeVO.setLocation("Test Location" + String.valueOf((int) (Math.random() * 100000)));
                storeVO.setLogoUrl("https://th.bing.com/th/id/OIP.Eib_F-NYXZeCsu355BnugwHaHa?rs=1&pid=ImgDetMain");
                mockMvc.perform(MockMvcRequestBuilders.post("/api/stores")
                                .contentType("application/json")
                                .header("token", token)
                                .content(objectMapper.writeValueAsString(storeVO))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.code", is("000")))
                                .andExpect(jsonPath("$.result", is(true)));
                Store storePO = storeRepository.findByName(storeVO.getName());
                Assertions.assertEquals(storePO.getLocation(), storeVO.getLocation());
                return storePO.toVO();
        }

        public static List<ProductVO> getAllProduct(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int storeId) throws Exception {
                // get
                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/products?storeId=" + storeId)
                                                .contentType("application/json")
                                                .header("token", userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code", is("000"))).andReturn();

                JsonNode readNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
                List<ProductVO> productList = objectMapper.readValue(readNode.traverse(),
                                new TypeReference<List<ProductVO>>() {
                                });
                return productList;
        }

        public static List<ProductVO> searchProduct(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int storeId, String name, Double minPrice, Double maxPrice) throws Exception {
                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/api/stores/" + storeId + "/search?name=" + name + "&minPrice="
                                                                + minPrice + "&maxPrice=" + maxPrice + "&category=FOOD")
                                                .contentType("application/json")
                                                .header("token", userToken))
                                .andExpect(status().isOk()).andExpect(jsonPath("$.code", is("000"))).andReturn();
                JsonNode readNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
                List<ProductVO> productList = objectMapper.readValue(readNode.traverse(),
                                new TypeReference<List<ProductVO>>() {
                                });
                return productList;
        }
}
