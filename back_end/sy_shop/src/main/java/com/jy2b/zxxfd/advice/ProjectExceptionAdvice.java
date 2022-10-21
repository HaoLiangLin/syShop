package com.jy2b.zxxfd.advice;

import com.jy2b.zxxfd.domain.dto.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResultVo doException(Exception e) {
        System.out.println("成功捕获一个异常：" + e.getMessage());
        e.printStackTrace();
        return ResultVo.fail(e.getMessage());
    }
}
