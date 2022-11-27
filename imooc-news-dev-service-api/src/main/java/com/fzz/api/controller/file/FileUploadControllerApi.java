package com.fzz.api.controller.file;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/fs")
public interface FileUploadControllerApi {

    @PostMapping("/uploadFace")
    public Object uploadFace(@RequestParam Long userId,  MultipartFile file) throws Exception;
}
