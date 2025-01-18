package com.caron.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PhotoUtil {

    //获取yaml中配置的上传路径属性
    @Value("${web.upload-path}")
    private String uploadPath;

    @Value("${web.service-path}")
    private String servicePath;

    /*
    * 存储图片到本地
    * */
    public String savePhoto(MultipartFile file) throws IOException {
        if (file != null) {
            // 1. 获取图片名称
            // 获取图片全名
            String originalFilename = file.getOriginalFilename();
            // 使用Spring的工具类来获取文件扩展名
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            // 制作电脑时间字符串，用于文件存储命名
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            String s = now.format(formatter);

            // 2. 制作图片的地址
            // 拼接保存图片的真实地址
            String saveUri= s + "." +fileExtension;
            String trueUrl=uploadPath + "/photo/"+  s + "." +fileExtension;

            // 3. 存储图片并返回图片地址
            // 将文件保存在 saveUri 路径
            try{
                System.err.println(trueUrl);
                file.transferTo(new File(trueUrl));
            }catch (Exception exception){
                System.err.println(exception);
            }
            return saveUri;
        }
        return null;
    }


    /*
    * 微服务中路径拼接需要带一个标识 photo/ 过网关
    * */
    public String getPhoto(String saveUri){
        if (saveUri != null){
            String serveLocation = servicePath;
            serveLocation = serveLocation.concat("photo/" + saveUri);
            return serveLocation;
        }else {
            return null;
        }
    }


}
