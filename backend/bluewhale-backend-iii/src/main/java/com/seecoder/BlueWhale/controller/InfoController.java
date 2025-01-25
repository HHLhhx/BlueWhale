package com.seecoder.BlueWhale.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.service.InfoService;
import com.seecoder.BlueWhale.vo.InfoVO;
import com.seecoder.BlueWhale.vo.ResultVO;

@RestController
@RequestMapping("/api/info")
public class InfoController {
    @Autowired
    InfoService infoService;

    @GetMapping
    @Access(roles = RoleEnum.CUSTOMER)
    public ResultVO<List<InfoVO>> getInfo() {
        return ResultVO.buildSuccess(infoService.getInfo());
    }
}
