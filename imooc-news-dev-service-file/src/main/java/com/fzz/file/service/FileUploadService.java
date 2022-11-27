package com.fzz.file.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileUploadService {

    public String uploadFdfs(MultipartFile file,String extName) throws Exception;
}
