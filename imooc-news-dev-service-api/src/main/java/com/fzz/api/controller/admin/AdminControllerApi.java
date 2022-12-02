package com.fzz.api.controller.admin;

import com.fzz.bo.AddNewAdminBo;
import com.fzz.bo.AdminUserLoginBo;
import com.fzz.common.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("/adminMng")
public interface AdminControllerApi {
    //adminMng/adminLogin
    @PostMapping("/adminLogin")
    public GraceJSONResult adminLogin(@RequestBody @Valid AdminUserLoginBo adminUserLoginBo,
                                      BindingResult bindingResult,
                                      HttpServletRequest request,
                                      HttpServletResponse response);

    @PostMapping("/adminLogout")
    public GraceJSONResult adminLogout(@RequestParam Long adminId,
                                HttpServletRequest request,
                                HttpServletResponse response);

    @PostMapping("/getAdminList")
    public GraceJSONResult getAdminList(@RequestParam Integer page,@RequestParam Integer pageSize);

    @PostMapping("/adminIsExist")
    public GraceJSONResult adminIsExist(@RequestParam String username);

    @PostMapping("/addNewAdmin")
    public GraceJSONResult addNewAdmin(@RequestBody AddNewAdminBo addNewAdminBo);

    @PostMapping("/adminFaceLogin")
    public GraceJSONResult adminFaceLogin(@RequestBody AdminUserLoginBo adminUserLoginBo,
                                          HttpServletRequest request,
                                          HttpServletResponse response);

}
