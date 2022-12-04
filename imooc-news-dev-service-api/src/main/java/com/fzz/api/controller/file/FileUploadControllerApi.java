package com.fzz.api.controller.file;

import com.fzz.bo.AddNewAdminBO;
import com.fzz.common.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RequestMapping("/fs")
@Api(value = "文件处理",tags = "文件处理")
public interface FileUploadControllerApi {

    @PostMapping("/uploadFace")
    @ApiOperation(value = "用户上传修改头像",notes = "用户上传修改头像")
    public GraceJSONResult uploadFace(@RequestParam Long userId,  MultipartFile file) throws Exception;

    @PostMapping("/uploadSomeFiles")
    @ApiOperation(value = "用户上传修改头像",notes = "用户上传修改头像")
    public GraceJSONResult uploadSomeFiles(@RequestParam Long userId,  MultipartFile[] files) throws Exception;





    @PostMapping("/uploadToGridFS")
    public GraceJSONResult uploadToGridFS(@RequestBody AddNewAdminBO addNewAdminBo) throws Exception;


    @GetMapping("/readInGridFS")
    public void readInGridFS(@RequestParam String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @GetMapping("/readFace64InGridFS")
    public GraceJSONResult readFaceGridFS(@RequestParam String faceId,HttpServletRequest request,HttpServletResponse response) throws Exception;
}
