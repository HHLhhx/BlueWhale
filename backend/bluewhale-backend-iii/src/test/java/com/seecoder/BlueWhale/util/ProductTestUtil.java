package com.seecoder.BlueWhale.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.vo.ProductVO;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductTestUtil {
        public static ProductVO createProductOk(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int storeId, String name, Double price, ProductRepository productRepository) throws Exception {
                ProductVO productVO = new ProductVO();
                productVO.setName(name);
                ArrayList<String> urls = new ArrayList<String>();
                urls.add("http://test.com/" + String.valueOf((int) (Math.random() * 100000)));
                productVO.setPhotoUrlList(urls);
                productVO.setPrice(price);
                productVO.setStoreId(storeId);
                productVO.setCategory(CategoryEnum.FOOD);
                // create
                mockMvc.perform(MockMvcRequestBuilders.post("/api/products").contentType("application/json")
                                .header("token", userToken)
                                .content(objectMapper.writeValueAsString(productVO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code", is("000")));
                Product product = productRepository.findByName(name);
                assertNotNull(product);
                assertTrue((product.getPhotoUrlList().size() == 1));
                assertEquals(urls.get(0), product.getPhotoUrlList().get(0));
                return product.toVO();
        }

        public static ProductVO getProductOk(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int productId, ProductRepository productRepository) throws Exception {
                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + productId)
                                .contentType("application/json")
                                .header("token", userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code", is("000")))
                                .andReturn();
                JsonNode readNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
                ProductVO product = objectMapper.readValue(readNode.traverse(), ProductVO.class);
                assertNotNull(product);
                Product productExpected = productRepository.findById(product.getId()).get();
                assertNotNull(productExpected);
                assertProductEquals(productExpected.toVO(), product);
                return product;
        }

        public static void addStock(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int productId, int num) throws Exception {
                // get
                mockMvc.perform(MockMvcRequestBuilders.post("/api/products/" + productId + "/stock?number=" + num)
                                .contentType("application/json")
                                .header("token", userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code", is("000")));
        }

        public static void assertProductEquals(ProductVO product1, ProductVO product2) throws Exception {
                assertEquals(product1.getPhotoUrlList().size(), product2.getPhotoUrlList().size());
                int photoSize = product1.getPhotoUrlList().size();
                for (int i = 0; i < photoSize; i++) {
                        assertEquals(product1.getPhotoUrlList().get(i), product2.getPhotoUrlList().get(i));
                }
        }
}
