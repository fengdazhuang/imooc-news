package com.fzz.file.controller;

import com.fzz.api.BaseController;
import com.fzz.api.controller.file.FileUploadControllerApi;
import com.fzz.bo.AddNewAdminBO;
import com.fzz.common.exception.CustomException;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.utils.FileUtils;
import com.fzz.file.FileResource;
import com.fzz.file.service.FileUploadService;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@RestController
public class FileUploadController extends BaseController implements FileUploadControllerApi {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private GridFSBucket gridFSBucket;


    @Override
    public GraceJSONResult uploadFace(Long userId, MultipartFile file) throws Exception{
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

    @Override
    public GraceJSONResult uploadSomeFiles(Long userId, MultipartFile[] files) throws Exception {
        List<String> filePathList=new ArrayList<>();
        if(files!=null&&files.length>0){
            for(MultipartFile file:files){
                if(file!=null){
                    String fileName=file.getOriginalFilename();
                    if(StringUtils.isNotBlank(fileName)){
                        String fileSplit[]=fileName.split("\\.");
                        String suffix=fileSplit[fileSplit.length-1];
                        if(!suffix.equalsIgnoreCase("png")&&!suffix.equalsIgnoreCase("jpg")
                                &&!suffix.equalsIgnoreCase("jpeg")){
                            continue;
                        }
                        String filePath=fileUploadService.uploadFdfs(file,suffix);
                        if(StringUtils.isNotBlank(filePath)){
                            filePathList.add(fileResource.getHost()+filePath);
                        }
                    }
                }
            }
            return GraceJSONResult.ok(filePathList);
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
    }

    @Override
    public GraceJSONResult uploadToGridFS(AddNewAdminBO addNewAdminBo) throws IOException {
        String img64 = addNewAdminBo.getImg64();
        byte[] bytes = new BASE64Decoder().decodeBuffer(img64.trim());
        ByteArrayInputStream arrayInputStream=new ByteArrayInputStream(bytes);
        ObjectId objectId = gridFSBucket.uploadFromStream(addNewAdminBo.getUsername()+".png",arrayInputStream);
        String idString = objectId.toString();

        return GraceJSONResult.ok(idString);
    }

    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(StringUtils.isBlank(faceId)||faceId.equalsIgnoreCase("null")){
            throw new CustomException(ResponseStatusEnum.SYSTEM_FILE_NOT_FOUND);
        }
        File file = readInGridFS(faceId);
        FileUtils.downloadFileByStream(response,file);

    }

    @Override
    public GraceJSONResult readFaceGridFS(String faceId,HttpServletRequest request ,HttpServletResponse response) throws Exception {
        File file=readInGridFS(faceId);
        String base64 = FileUtils.fileToBase64(file);

        return GraceJSONResult.ok(base64);
    }


    public File readInGridFS(String faceId) throws Exception {
        Bson bson = Filters.eq("_id", new ObjectId(faceId));
        GridFSFindIterable gridFSFiles = gridFSBucket.find(bson);
        GridFSFile gridFSFile = gridFSFiles.first();
        if(gridFSFile==null){
            throw new CustomException(ResponseStatusEnum.SYSTEM_FILE_NOT_FOUND);
        }
        String filename = gridFSFile.getFilename();
        File dir = new File("/workspace/temp_face");
        System.out.println(dir.getAbsolutePath());
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File("/workspace/temp_face/" + filename);
        OutputStream fileOutputStream = new FileOutputStream(file);

        gridFSBucket.downloadToStream(new ObjectId(faceId),fileOutputStream);

        return file;

    }
}
