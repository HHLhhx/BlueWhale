package com.seecoder.BlueWhale.exception;

import com.seecoder.BlueWhale.serviceImpl.UserServiceImpl;
import com.seecoder.BlueWhale.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author: DingXiaoyu
 * @Date: 0:26 2023/11/26
 * 这个类能够接住项目中所有抛出的异常，
 * 使用了RestControllerAdvice切面完成，
 * 表示所有异常出现后都会通过这里。
 * 这个类将异常信息封装到ResultVO中进行返回。
*/
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = BlueWhaleException.class)
    public ResultVO<String> handleAIExternalException(BlueWhaleException e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        return ResultVO.buildFailure(e.getMessage());
    }
}
