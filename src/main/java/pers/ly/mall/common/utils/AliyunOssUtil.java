package pers.ly.mall.common.utils;

import cn.hutool.core.lang.UUID;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.exception.OssUploadException;
import pers.ly.mall.common.properties.AliyunOssProperties;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class AliyunOssUtil {
    private final AliyunOssProperties aliyunOssProperties;

    AliyunOssUtil(AliyunOssProperties properties){
        this.aliyunOssProperties=properties;
    }

    /**
     * 上传文件到阿里云OSS
     * @param path OSS存储路径（如："upload/avatar/"）
     * @param file 待上传的文件
     * @return 完整的OSS文件访问URL
     * @throws ClientException OSS客户端异常
     */
    public String upload(String path, MultipartFile file) throws ClientException {
        // 2. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            log.error("上传失败：文件为空");
            throw new IllegalArgumentException("待上传文件不能为空");
        }

        // 获取配置信息
        String accessKeyId = aliyunOssProperties.getAccessKeyId();
        String accessKeySecret = aliyunOssProperties.getAccessKeySecret();
        String endpoint = aliyunOssProperties.getEndpoint();
        String bucketName = aliyunOssProperties.getBucketName();
        String region = aliyunOssProperties.getRegion();

        // 3. 处理文件名：拼接UUID避免重复，防止文件覆盖
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID(true) + "_" + originalFilename;
        // 完整的OSS对象路径（不含Bucket名称）
        String objectName = path.trim() + "/" + uniqueFileName;

        // OSS客户端实例
        OSS ossClient = null;
        InputStream inputStream = null;

        try {
            // 4. 从配置类获取凭证，替换环境变量获取方式
            DefaultCredentialProvider credentialProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

            // 配置客户端参数
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

            // 创建OSSClient实例
            ossClient = OSSClientBuilder.create()
                    .endpoint(endpoint)
                    .credentialsProvider(credentialProvider)
                    .clientConfiguration(clientBuilderConfiguration)
                    .region(region)
                    .build();

            // 5. 正确获取MultipartFile的输入流，替换不存在的filePath
            inputStream = file.getInputStream();

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            // 执行上传
            ossClient.putObject(putObjectRequest);
            // 6. 拼接完整的OSS文件访问URL并返回
            return String.format("https://%s.%s/%s", bucketName, endpoint, objectName);
        } catch (OSSException oe) {
            throw new OssUploadException(ErrorConstant.NO_REACH_SERVER);
        } catch (ClientException ce) {
            throw new OssUploadException(ErrorConstant.REJECT_BY_SERVER);
        } catch (IOException e) {
            throw new OssUploadException(ErrorConstant.FILE_WRITE_ERROR);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
