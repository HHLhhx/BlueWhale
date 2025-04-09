package com.seecoder.BlueWhale.util;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("aliyun.oss")
public class OssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(OssUtil.class);

    public String upload(String objectName, InputStream inputStream) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
        try {
            ossClient.putObject(putObjectRequest);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        String url = ossClient.generatePresignedUrl(bucketName, objectName, Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant())).toString().split("\\?Expires")[0];
        logger.info("Upload image {}", url);
        return url;
    }

}
