package com.leyou.upload.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg","image/png");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        //1. 文件类型
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            LOGGER.info("文件上传失败，{}，文件类型不合法",originalFilename);
            return null;
        }
        try {
            //2. 校验文件的内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            //在这块也可以加上判断图片长宽，如果不是图片那么他一定没有长宽
            if(bufferedImage ==null){
                LOGGER.info("文件上传失败，{}，文件内容不合法",originalFilename);
                return  null;
            }
            //3. 保存到服务器
            file.transferTo(new File("E:\\code\\images\\"+originalFilename));
            //4. 返回url路径
            return "http://image.leyou.com/"+originalFilename;
        } catch (IOException e) {
            LOGGER.info("文件上传失败，{}，服务器异常",originalFilename);
            e.printStackTrace();
        }
        return  null;
    }
}
