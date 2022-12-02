package com.fzz.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.admin.service.AdminService;
import com.fzz.api.BaseController;
import com.fzz.api.controller.admin.AdminControllerApi;
import com.fzz.bo.AddNewAdminBo;
import com.fzz.bo.AdminUserLoginBo;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.common.utils.FaceVerifyUtils;
import com.fzz.common.utils.RedisUtil;
import com.fzz.pojo.AdminUser;
import com.fzz.vo.QueryAdminVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AdminController extends BaseController implements AdminControllerApi {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FaceVerifyUtils faceVerifyUtils;

    @Override
    public GraceJSONResult adminLogin(AdminUserLoginBo adminUserLoginBo,
                             BindingResult bindingResult,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        if(bindingResult.hasErrors()){
            Map<String, String> errors=getErrors(bindingResult);
            return GraceJSONResult.errorMap(errors);
        }
        String username=adminUserLoginBo.getUsername();

        AdminUser adminUser=adminService.getAdminByUsername(username);
        if(adminUser!=null){
            String password=adminUserLoginBo.getPassword();
            boolean result=BCrypt.checkpw(password,adminUser.getPassword());
            if(result){
                doLoginSettings(adminUser,request,response);
                return GraceJSONResult.ok();
            }

        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);

    }

    @Override
    public GraceJSONResult adminLogout(Long adminId,HttpServletRequest request,HttpServletResponse response) {

        redisUtil.del(REDIS_ADMIN_TOKEN+":"+adminId);
        setCookie(request,response,"aid","",COOKIE_DELETE);
        setCookie(request,response,"atoken","",COOKIE_DELETE);
        setCookie(request,response,"aname","",COOKIE_DELETE);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getAdminList(Integer page, Integer pageSize) {
        Page<AdminUser> adminUserPage=new Page<>(page,pageSize);
        adminService.page(adminUserPage);
        Page<QueryAdminVo> queryAdminVoPage=new Page<>();
        BeanUtils.copyProperties(adminUserPage,queryAdminVoPage,"records");
        List<AdminUser> records=adminUserPage.getRecords();

        List<QueryAdminVo> list=records.stream().map(((item)->{
            QueryAdminVo queryAdminVo=new QueryAdminVo();
            BeanUtils.copyProperties(item,queryAdminVo);
            return queryAdminVo;
        })).collect(Collectors.toList());
        queryAdminVoPage.setRecords(list);

        return GraceJSONResult.ok(queryAdminVoPage);

    }

    @Override
    public GraceJSONResult adminIsExist(String username) {
        if(StringUtils.isNotBlank(username)){
            AdminUser user=adminService.getAdminByUsername(username);
            if(user!=null){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
            }
            return GraceJSONResult.ok();
        }
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult addNewAdmin(AddNewAdminBo addNewAdminBo) {
        if(StringUtils.isBlank(addNewAdminBo.getUsername())){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        if(StringUtils.isBlank(addNewAdminBo.getAdminName())){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NAME_NULL_ERROR);
        }
        String username=addNewAdminBo.getUsername();
        if(adminService.getAdminByUsername(username)!=null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
        String password=addNewAdminBo.getPassword();
        String confirmPassword=addNewAdminBo.getConfirmPassword();
        if(StringUtils.isBlank(addNewAdminBo.getImg64())){
            if(StringUtils.isBlank(password)||
                    StringUtils.isBlank(confirmPassword)){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
            if(password.equalsIgnoreCase(confirmPassword)){
                boolean resut=adminService.addNewAdmin(addNewAdminBo);
                if(resut){
                    return GraceJSONResult.ok();
                }
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_CREATE_ERROR);
            }
        }
        boolean resut = adminService.addNewAdmin(addNewAdminBo);
        if (resut) {
            return GraceJSONResult.ok();
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_CREATE_ERROR);

    }

    @Override
    public GraceJSONResult adminFaceLogin(AdminUserLoginBo adminUserLoginBo,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        String username = adminUserLoginBo.getUsername();
        String img64 = adminUserLoginBo.getImg64();
        if(StringUtils.isBlank(username)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        if(StringUtils.isBlank(img64)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }

        AdminUser admin = adminService.getAdminByUsername(username);
        if(admin==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
        String faceId = admin.getFaceId();
        if(StringUtils.isBlank(faceId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NOT_EXIST);
        }

        String url = "http://files.imoocnews.com:8004/fs/readFace64InGridFS?faceId=" + faceId;
        ResponseEntity<GraceJSONResult> entity = restTemplate.getForEntity(url, GraceJSONResult.class);
        String data = (String) entity.getBody().getData();

        boolean result = faceVerifyUtils.faceVerify(1, img64, data,  70);

        if(!result){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }
        doLoginSettings(admin,request,response);

        return GraceJSONResult.ok();
    }



    public void doLoginSettings(AdminUser admin,HttpServletRequest request,HttpServletResponse response){
        String atoken=UUID.randomUUID().toString();
        redisUtil.set(REDIS_ADMIN_TOKEN+":"+admin.getId(),atoken);
        setCookie(request,response,"aid",String.valueOf(admin.getId()),COOKIE_MONTH);
        setCookie(request,response,"atoken",atoken,COOKIE_MONTH);
        setCookie(request,response,"aname",admin.getAdminName(),COOKIE_MONTH);
    }
}
