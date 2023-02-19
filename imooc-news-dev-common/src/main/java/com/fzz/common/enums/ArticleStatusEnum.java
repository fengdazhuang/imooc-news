package com.fzz.common.enums;

public enum ArticleStatusEnum {

    AI_REVIEW(1,"文章已提交，等待审核"),
    MANAGER_REVIEW(2,"机审结束，等待人工审核"),
    PUBLISH(3,"审核完毕，已成功发布"),
    FAILD(4,"文章审核未通过"),
    WITHDRAW(5,"文章已撤回"),

    PASS(1,"审核通过"),
    NOT_PASS(0,"审核未通过");



    private final Integer type;

    private final String msg;

    ArticleStatusEnum(Integer type,String msg){
        this.msg=msg;
        this.type=type;
    }

    public Integer type(){
        return type;
    }

    public String msg(){
        return msg;
    }

    public static boolean isArticleStatusValid(Integer id){
        if(id!=null){
            if(id==FAILD.type||id== AI_REVIEW.type||id== MANAGER_REVIEW.type||id== PUBLISH.type||id== WITHDRAW.type){
                return true;
            }
        }
        return false;
    }

}
