package com.leyou.upload.web;

import com.leyou.upload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * <code>UploadController</code>
 * </p>
 *  图片上传
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/28 19:54
 */
@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

   /**
    *  图片上传
    * @description TODO
    * @author huiwang45@iflytek.com
    * @date 2019/11/28 19:54
    * @param  file
    * @return url 文件上传的路径
    */
    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = this.uploadService.uploadImage(file);
        if (StringUtils.isBlank(url)) {
            // url为空，证明上传失败
            return ResponseEntity.badRequest().build();
        }
        // 返回200，并且携带url路径
        //return ResponseEntity.ok(url);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }
}
