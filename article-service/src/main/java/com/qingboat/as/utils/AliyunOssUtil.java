package com.qingboat.as.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.qingboat.base.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Slf4j
public class AliyunOssUtil {


    // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
    private static String OSS_ENDPOINT = "oss-cn-beijing.aliyuncs.com";
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    private static String OSS_ACCESS_KEY_ID = "LTAI4GGFALNnM1MDQhoKAF1J";
    private static String OSS_ACCESS_KEY_SECRET = "xb1lhVTpM4FeqtRsv9aQUVc9GtoxUw";
    private static String OSS_BUCKET_NAME  = "qingboat";


    public static String upload(MultipartFile file,String fileName) throws IOException {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
        InputStream inputStream = file.getInputStream();
        String filePath = new StringBuilder("media/")
                .append(DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYYMM))
                .append("/")
                .append(fileName)
                .toString();
        PutObjectResult rst = ossClient.putObject(OSS_BUCKET_NAME, filePath, file.getInputStream());

        if (inputStream!=null){
            inputStream.close();
        }
        // 关闭OSSClient。
        ossClient.shutdown();

        String url = new StringBuilder("https://")
                .append(OSS_BUCKET_NAME)
                .append(".")
                .append(OSS_ENDPOINT)
                .append("/")
                .append(filePath)
                .toString();

        return  url;

    }


}
