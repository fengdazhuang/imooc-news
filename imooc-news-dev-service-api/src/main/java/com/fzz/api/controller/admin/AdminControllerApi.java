package com.fzz.api.controller.admin;

import com.fzz.bo.AdminUserLoginBo;
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
    public Object adminLogin(@RequestBody @Valid AdminUserLoginBo adminUserLoginBo,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             HttpServletResponse response);

    @PostMapping("/adminLogout")
    public Object adminLogout(@RequestParam Long adminId,
                                HttpServletRequest request,
                                HttpServletResponse response);


}
