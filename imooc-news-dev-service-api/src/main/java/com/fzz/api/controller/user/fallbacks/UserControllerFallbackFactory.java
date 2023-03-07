package com.fzz.api.controller.user.fallbacks;

import com.fzz.api.controller.user.UserControllerApi;
import com.fzz.bo.UpdateUserInfoBO;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserControllerFallbackFactory implements FallbackFactory {

    @Override
    public Object create(Throwable cause) {
        return new UserControllerApi() {
            @Override
            public GraceJSONResult getAccountInfo(Long userId) {
                System.out.println("触发全局熔断降级getAccountInfo");
                return null;
            }

            @Override
            public GraceJSONResult getUserInfo(Long userId) {
                System.out.println("触发全局熔断降级getUserInfo");
                return null;
            }

            @Override
            public GraceJSONResult updateUserInfo(UpdateUserInfoBO userInfoBO) {
                System.out.println("触发全局熔断降级updateUserInfo");
                return null;
            }

            @Override
            public GraceJSONResult queryBaseInfoByIds(String userIds) {
                System.out.println("触发全局熔断降级queryBaseInfoByIds");
                return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_BUSY_ERROR);
            }
        };
    }
}
