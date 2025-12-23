package pers.ly.mall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.ly.mall.common.constant.ErrorConstant;
import pers.ly.mall.common.entity.UserInfo;
import pers.ly.mall.common.exception.OssUploadException;
import pers.ly.mall.common.utils.AliyunOssUtil;
import pers.ly.mall.user.mapper.UserInfoMapper;
import pers.ly.mall.user.service.UserInfoService;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    private final AliyunOssUtil aliyunOssUtil;
    UserInfoServiceImpl(AliyunOssUtil aliyunOssUtil) {
        this.aliyunOssUtil = aliyunOssUtil;
    }


    /**
     * 上传头像文件
     * @param avatar 头像
     * @return 上传地址
     */
    @Override
    public String updateAvatar(MultipartFile avatar) {
        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename != null && (originalFilename.endsWith("jpg") ||
                originalFilename.endsWith("jpeg") ||
                originalFilename.endsWith("png") ||
                originalFilename.endsWith("gif"))) {
            return aliyunOssUtil.upload("avatar", avatar);
        }
        else {
            throw new OssUploadException(ErrorConstant.FILE_IS_VALID);
        }
    }
}
