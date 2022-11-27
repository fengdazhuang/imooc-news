package com.fzz.file.service.impl;

import com.fzz.file.service.FileUploadService;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Override
    public String uploadFdfs(MultipartFile file, String extName) throws Exception {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extName, null);

        return storePath.getFullPath();
    }
}
