package com.seecoder.BlueWhale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.vo.UserVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegisterUseCaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional(rollbackOn = Exception.class)
    void register() throws Exception {
        UserVO userVO = new UserVO();
        userVO.setPassword("123456");
        userVO.setAddress("NJU");
        userVO.setName("CHY");
        userVO.setPhone("16666666666");
        userVO.setRole(RoleEnum.CUSTOMER);
        mockMvc.perform(post("/api/users/register", 42L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userVO)))
                .andExpect(status().isOk());

        User userPO = userRepository.findByPhone("16666666666");
        Assertions.assertThat(userPO.getName()).isEqualTo("CHY");
    }
}
