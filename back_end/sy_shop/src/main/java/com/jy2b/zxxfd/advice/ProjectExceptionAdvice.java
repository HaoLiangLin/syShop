package com.jy2b.zxxfd.advice;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResultVO doException(Exception e) {
        System.out.println("成功捕获一个异常：" + e.getMessage());
        e.printStackTrace();
        return ResultVO.fail(e.getMessage());
    }
}
