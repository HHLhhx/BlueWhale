package com.seecoder.BlueWhale.exception;

import com.seecoder.BlueWhale.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
