package com.fzz.file.controller;

import com.fzz.api.BaseController;
import com.fzz.api.controller.file.FileUploadControllerApi;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.result.ResponseStatusEnum;
import com.fzz.file.FileResource;
import com.fzz.file.service.FileUploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController extends BaseController implements FileUploadControllerApi {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileResource fileResource;


    @Override
    public Object uploadFace(Long userId, MultipartFile file) throws Exception{
        if(StringUtils.isBlank(String.valueOf(userId))|| file.isEmpty()){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }else{
            String fileName=file.getOriginalFilename();
            if(StringUtils.isNotBlank(fileName)){
                String fileSplit[]=fileName.split("\\.");
                String suffix=fileSplit[fileSplit.length-1];
                if(!suffix.equalsIgnoreCase("png")&&!suffix.equalsIgnoreCase("jpg")
                        &&!suffix.equalsIgnoreCase("jpeg")){
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }
                String filePath=fileUploadService.uploadFdfs(file,suffix);
                if(StringUtils.isNotBlank(filePath)){
                    return GraceJSONResult.ok(fileResource.getHost()+filePath);
                }else{
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
                }
            }else{
                return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        }

    }
}
