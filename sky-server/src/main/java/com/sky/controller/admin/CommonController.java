package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "Common Port")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * file
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "upload Files")
    public Result<String> uploead (MultipartFile file) {
        log.info("upload Files {}, ...", file);
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), UUID.randomUUID().toString() +
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
            );
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("failed upload Files {}, IOException", file);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
