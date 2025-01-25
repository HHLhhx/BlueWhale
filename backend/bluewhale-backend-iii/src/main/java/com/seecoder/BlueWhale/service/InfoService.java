package com.seecoder.BlueWhale.service;

import java.util.List;

import com.seecoder.BlueWhale.enums.InfoEnum;
import com.seecoder.BlueWhale.vo.InfoVO;

public interface InfoService {
    List<InfoVO> getInfo();

    Boolean addInfo(Integer uid, InfoEnum type, String message);
}
