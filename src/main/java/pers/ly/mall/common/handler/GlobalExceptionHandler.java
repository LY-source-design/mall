package pers.ly.mall.common.handler;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.ly.mall.common.entity.result.Result;
import pers.ly.mall.common.exception.BaseException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler
    public Result Exception(BaseException e){
        System.err.println(e.getMessage());
        return Result.error(e.getMessage());
    }

}
