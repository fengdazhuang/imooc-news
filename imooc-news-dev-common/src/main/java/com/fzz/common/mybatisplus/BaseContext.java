package com.fzz.common.mybatisplus;

@Deprecated
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrent(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrent(){
        return threadLocal.get();
    }
}