package com.seecoder.BlueWhale.util;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.vo.CommentVO;
import com.seecoder.BlueWhale.vo.OrderVO;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.valueOf;
import static org.mockito.Mockito.timeout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.BoldAction;

public class OrderTestUtil {
        public static Integer createOrder(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int productId) throws Exception {
                OrderVO orderVO = new OrderVO();
                orderVO.setDeliveryMethod(DeliveryEnum.PICKUP);
                orderVO.setNum(1);
                orderVO.setProductId(productId);
                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/orders").contentType("application/json")
                                                .header("token", userToken)
                                                .content(objectMapper.writeValueAsString(orderVO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code", is("000"))).andReturn();
                JsonNode resultNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
                return Integer.valueOf(objectMapper.treeToValue(resultNode, String.class));
        }

        public static String payOrder(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int orderId) throws Exception {
                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/orders/" + orderId + "/pay?isDirectPay=true")
                                                .contentType("application/json")
                                                .header("token", userToken)
                                                .content(objectMapper.writeValueAsString(new ArrayList<Integer>())))
                                .andExpect(status().isOk())
                                .andReturn();
                return result.getResponse().getContentAsString();
        }

        public static CommentVO commandOrderOk(MockMvc mockMvc, ObjectMapper objectMapper, String userToken,
                        int orderId, Double rating, CommentRepository commentRepository) throws Exception {
                                Comment comment = new Comment();
                                comment.setText("test: " + String.valueOf((int)(Math.random() * 1000000)));
                                comment.setRating(rating);
                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/orders/" + orderId + "/comment")
                                                .contentType("application/json")
                                                .header("token", userToken)
                                                .content(objectMapper.writeValueAsString(comment)))
                                .andExpect(status().isOk())
                                .andReturn();
                JsonNode resultNode = objectMapper.readTree(result.getResponse().getContentAsString()).get("result");
                assertTrue(objectMapper.treeToValue(resultNode, Boolean.class));
                Comment commentExpected = commentRepository.findByOrderId(orderId);
                assertNotNull(commentExpected);
                assertEquals(comment.getText(), commentExpected.getText());
                return commentExpected.toVO();
        }
}
