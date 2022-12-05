package com.fzz.common.exception;

import com.fzz.common.enums.ResponseStatusEnum;
import lombok.Data;

@Data
public class CustomException extends RuntimeException{

    private ResponseStatusEnum responseStatusEnum;

    //重写
    public CustomException(ResponseStatusEnum responseStatusEnum){
        super(responseStatusEnum.status()+":"+responseStatusEnum.msg());
        this.responseStatusEnum=responseStatusEnum;
    }
}
