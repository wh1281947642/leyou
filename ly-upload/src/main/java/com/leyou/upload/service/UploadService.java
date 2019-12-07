package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * <code>UploadService</code>
 * </p>
 *  图片上传
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/28 19:55
 */
@Service
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private FastFileStorageClient storageClient;

    // 支持的文件类型
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/png", "image/jpeg","image/gif");

    /**
     * 图片上传
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/11/28 19:55
     * @param  file
     * @return url
     */
    public String uploadImage(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        //校验文件类型
        //String afterLast = StringUtils.substringAfterLast(originalFilename, ",");
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            //logger.info("文件类型不合法:" + originalFilename);
            logger.info("文件类型不合法:{}",originalFilename);
            return null;
        }
        try {
            //校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                logger.info("文件内容不合法:{}",originalFilename);
                return null;
            }
            //保存到服务器
            //file.transferTo(new File("F:\\heima\\upload\\" + originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            //返回url，进行回显
            //return  "http://image.leyou.com/" + originalFilename;
            return  "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            logger.info("服务器内部错误:" +originalFilename );
            e.printStackTrace();
        }
        return null;
    }
}