package com.fzz.api.controller.file;

import com.fzz.bo.AddNewAdminBo;
import com.fzz.common.result.GraceJSONResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RequestMapping("/fs")
public interface FileUploadControllerApi {

    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam Long userId,  MultipartFile file) throws Exception;

    @PostMapping("/uploadToGridFS")
    public GraceJSONResult uploadToGridFS(@RequestBody AddNewAdminBo addNewAdminBo) throws Exception;


    @GetMapping("/readInGridFS")
    public void readInGridFS(@RequestParam String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @GetMapping("/readFace64InGridFS")
    public GraceJSONResult readFaceGridFS(@RequestParam String faceId,HttpServletRequest request,HttpServletResponse response) throws Exception;
}
