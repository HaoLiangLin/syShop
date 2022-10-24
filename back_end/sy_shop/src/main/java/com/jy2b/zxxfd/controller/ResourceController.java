package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.contants.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.FileInputStream;

@RestController
@CrossOrigin
@RequestMapping("/resources")
@Api(tags = "静态资源接口")
public class ResourceController {
    @GetMapping(value = "/image",produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "获取图片")
    public byte[] getUploadImage(@RequestParam("name") String name) {
        String imagePath = SystemConstants.UPLOAD_IMAGE_PATH + name;

        File file = new File(imagePath);
        byte[] bytes = new byte[0];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes, 0, fileInputStream.available());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
