package com.fzz.common.exception;


import com.fzz.common.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public Object exceptionHandler(SQLIntegrityConstraintViolationException ex){
//        log.error(ex.getMessage());
//        if(ex.getMessage().contains("Duplicate entry")){
//            String[] split = ex.getMessage().split(" ");
//            String msg = split[2] + "已存在";
//            return R.error(msg);
//        }
//        return R.error("该账号已经存在");
//    }


   /**
     * 异常处理方法
    */
    @ExceptionHandler(CustomException.class)
    public GraceJSONResult exceptionHandler(CustomException exception){
        return GraceJSONResult.exception(exception.getResponseStatusEnum());
    }
}
