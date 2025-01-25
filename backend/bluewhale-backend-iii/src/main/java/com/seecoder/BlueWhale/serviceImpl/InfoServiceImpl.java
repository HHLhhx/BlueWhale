package com.seecoder.BlueWhale.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import com.seecoder.BlueWhale.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seecoder.BlueWhale.enums.InfoEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Info;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.InfoRepository;
import com.seecoder.BlueWhale.service.InfoService;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.vo.InfoVO;

@Service
public class InfoServiceImpl implements InfoService {
    @Autowired
    InfoRepository infoRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(InfoServiceImpl.class);

    @Override
    public List<InfoVO> getInfo() {
        User user = securityUtil.getCurrentUser();
        if (user == null) {
            throw BlueWhaleException.userNotExist();
        }

        return infoRepository.findAllByUid(user.getId()).stream().map(Info::toVO).collect(Collectors.toList());
    }

    @Override
    public Boolean addInfo(Integer uid, InfoEnum type, String message) {
        Info newInfo = new Info(uid, type, message);
        infoRepository.save(newInfo);
        logger.info(String.format("add info %s for user %s", message, userRepository.findById(uid)));
        return true;
    }
}
