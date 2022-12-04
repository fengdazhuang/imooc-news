package com.fzz.api.controller.user;

import com.fzz.common.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


@RequestMapping("/appUser")
@Api(value = "管理用户的信息",tags = "管理用户的信息")
public interface AppUserControllerApi {

    @PostMapping("/queryAll")
    @ApiOperation(value = "分页查询所有用户",notes = "分页查询所有用户")
    public GraceJSONResult queryAll(@RequestParam String nickname,
                                    @RequestParam Integer status,
                                    @RequestParam Date startDate,
                                    @RequestParam Date endDate,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);

    @PostMapping("/userDetail")
    @ApiOperation(value = "管理员查看用户信息",notes = "管理员查看用户信息")
    public GraceJSONResult getUserDetail(@RequestParam Long userId);

    @PostMapping("/freezeUserOrNot")
    @ApiOperation(value = "冻结或解封用户",notes = "冻结或解封用户")
    public GraceJSONResult freezeUserOrNot(@RequestParam Long userId,@RequestParam Integer doStatus);
}
