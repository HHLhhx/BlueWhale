package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.vo.ResultVO;
import com.seecoder.BlueWhale.vo.SafeUserVO;
import com.seecoder.BlueWhale.vo.UserVO;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResultVO<Boolean> register(@RequestBody UserVO userVO) {
        return ResultVO.buildSuccess(userService.register(userVO));
    }

    @PostMapping("/login")
    public ResultVO<String> login(@RequestParam("phone") String phone, @RequestParam("password") String password) {
        return ResultVO.buildSuccess(userService.login(phone, password));
    }

    @GetMapping
    public ResultVO<UserVO> getInformation() {
        return ResultVO.buildSuccess(userService.getInformation());
    }

    @GetMapping("/{id}")
    public ResultVO<SafeUserVO> getUser(@PathVariable(value = "id") String id) {
        return ResultVO.buildSuccess(userService.getUser(Integer.parseInt(id)));
    }

    @PostMapping
    public ResultVO<Boolean> updateInformation(@RequestBody UserVO userVO) {
        return ResultVO.buildSuccess(userService.updateInformation(userVO));
    }
}
