package com.jy2b.zxxfd.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.jy2b.zxxfd.contants.SystemConstants;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author 林武泰
 * 文件操作工具类
 */
public class UploadUtils {
    /**
     * 保存文件
     * @param file 文件
     * @param savePath 保存路径
     * @return ResultVO
     */
    public static ResultVO saveFile(MultipartFile file, String savePath) {
        try {
            // 判断文件是否为空
            if (file.isEmpty()) {
                return ResultVO.fail("文件未上传");
            }

            // 获取文件原始名称以及后缀名
            String originalFilename = file.getOriginalFilename();
            String fileName = createNewFileName(originalFilename, savePath);

            // 保存文件
            file.transferTo(new File(SystemConstants.UPLOAD_IMAGE_PATH, fileName));
            // 返回文件保存路径
            return ResultVO.ok(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            // 失败，返回错误信息
            return ResultVO.fail("文件上传失败");
        }
    }

    public static ResultVO saveFiles(MultipartFile[] files, String savePath) {
        if (files.length == 0) {
            return ResultVO.fail("上传文件不能为空");
        }
        String fileNames = "";
        try {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                // 获取文件原始名称以及后缀名
                String originalFilename = file.getOriginalFilename();
                String fileName = createNewFileName(originalFilename, savePath);

                // 保存文件
                file.transferTo(new File(SystemConstants.UPLOAD_IMAGE_PATH, fileName));
                // 返回文件保存路径
                fileNames = fileNames.concat("," + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 失败，返回错误信息
            return ResultVO.fail("文件上传失败");
        }

        if (StrUtil.isBlank(fileNames)) {
            return ResultVO.fail("文件上传失败");
        }

        fileNames = fileNames.substring(1);
        return ResultVO.ok(fileNames, "文件上传成功");
    }

    /**
     * 删除文件
     * @param fileName 图片名称
     */
    public static void deleteFile(String fileName) {
        File file = new File(SystemConstants.UPLOAD_IMAGE_PATH, fileName);
        if (file.isDirectory()) {
            return;
        }
        FileUtil.del(file);
    }

    public static void deleteFiles(String images) {
        if (StrUtil.isBlank(images)) {
            return;
        }
        String[] split = images.split(",");
        for (String s : split) {
            if (StrUtil.isNotBlank(s)) {
                deleteFile(s);
            }
        }
    }

    private static String createNewFileName(String originalFilename, String savePath) {
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(SystemConstants.UPLOAD_IMAGE_PATH, StrUtil.format(savePath + "/{}/{}", d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format(savePath +"/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
