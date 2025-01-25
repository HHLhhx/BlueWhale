package com.seecoder.BlueWhale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.util.HtmlOpenUtil;
import com.seecoder.BlueWhale.util.OrderTestUtil;
import com.seecoder.BlueWhale.util.ProductTestUtil;
import com.seecoder.BlueWhale.util.StoreTestUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.util.UserTestUtil;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderUseCaseTest {
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

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional(rollbackOn = Exception.class)
    void pay() throws Exception {
        String managerToken = UserTestUtil.createManagerOk(mockMvc, objectMapper, tokenUtil, userRepository);
        StoreVO storeVO = StoreTestUtil.createStoreOk(mockMvc, objectMapper, managerToken, storeRepository, "test");
        String staffToken = UserTestUtil.createStaffOk(mockMvc, objectMapper, tokenUtil, userRepository,
                storeVO.getId());
        ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(), "test", 10.0,
                productRepository);
        List<ProductVO> productsList = StoreTestUtil.getAllProduct(mockMvc, objectMapper, staffToken, storeVO.getId());
        assertEquals(productsList.size(), 1);
        ProductVO productVO = productsList.get(0);
        ProductTestUtil.addStock(mockMvc, objectMapper, staffToken, productVO.getId(), 1);
        String userToken = UserTestUtil.createCustomerOk(mockMvc, objectMapper, tokenUtil, userRepository);
        int orderId = OrderTestUtil.createOrder(mockMvc, objectMapper, userToken, productVO.getId());
        String retHtml = OrderTestUtil.payOrder(mockMvc, objectMapper, userToken, orderId);
        System.out.println("开始进行支付宝支付..."); // here we have html to ali pay
        // 支付宝回复html
        System.out.println(retHtml);
//        assertTrue(HtmlOpenUtil.openHtml(retHtml));
        // 评论
//        OrderTestUtil.commandOrderOk(mockMvc, objectMapper, userToken, orderId, 3.0, commentRepository);
    }
}
