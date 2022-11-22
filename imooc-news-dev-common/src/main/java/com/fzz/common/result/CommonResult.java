package com.fzz.common.result;

@Deprecated
public class CommonResult<T> {

    int code;

    String message;

    T data;

    public CommonResult<T> success(T data){
        CommonResult<T> result = new CommonResult<>();
        this.code=1;
        this.data=data;
        return result;
    }

    public CommonResult<T> error(String message){
        CommonResult<T> result = new CommonResult<>();
        this.code=0;
        this.message=message;
        return result;
    }



}
