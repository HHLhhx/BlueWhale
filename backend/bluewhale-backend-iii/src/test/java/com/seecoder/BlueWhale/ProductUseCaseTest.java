package com.seecoder.BlueWhale;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.util.ProductTestUtil;
import com.seecoder.BlueWhale.util.StoreTestUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.util.UserTestUtil;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductUseCaseTest {

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
    void addStock() throws Exception {
        String managerToken = UserTestUtil.createManagerOk(mockMvc, objectMapper, tokenUtil, userRepository);
        StoreVO storeVO = StoreTestUtil.createStoreOk(mockMvc, objectMapper, managerToken, storeRepository,
                "test");
        String staffToken = UserTestUtil.createStaffOk(mockMvc, objectMapper, tokenUtil, userRepository,
                storeVO.getId());
        ProductVO product1 = ProductTestUtil.createProductOk(mockMvc, objectMapper, staffToken, storeVO.getId(),
                "test3",
                10.23, productRepository);
        ProductVO product1Get1 = ProductTestUtil.getProductOk(mockMvc, objectMapper, managerToken, product1.getId(), productRepository);
        // 未添加库存时候 - 库存为0
        assertEquals(product1Get1.getStock().intValue(), 0);
        ProductTestUtil.addStock(mockMvc, objectMapper, staffToken, product1Get1.getId(), 3);
        ProductVO product1Get2 = ProductTestUtil.getProductOk(mockMvc, objectMapper, managerToken, product1.getId(), productRepository);
        // 添加了库存时候 - 库存为3
        assertEquals(product1Get2.getStock().intValue(), 3);
    }

}
