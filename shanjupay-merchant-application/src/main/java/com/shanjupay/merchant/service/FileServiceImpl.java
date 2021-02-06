package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.QiNiuUtil;
import com.shanjupay.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/*****
 *@Author NJL
 *@Description 文件实现
 */

@Service
@Slf4j
@RefreshScope
public class FileServiceImpl implements FileService{


    @Value("${qiNiu.accessKey}")
    private String  accessKey;

    @Value("${qiNiu.secretKey}")
    private String  secretKey;

    @Value("${qiNiu.bucket}")
    private String  bucket;

    @Value("${qiNiu.url}")
    private String  url;


    /****
     * 证件照上传
     * @param file 证件照
     * @return 绝对路径
     */
    @Override
    public String upload(MultipartFile file) throws BusinessException {
        String originalFilename = file.getOriginalFilename();
        String fileName = null;
        if (!StringUtil.isBlank(originalFilename)){
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") - 1);
            fileName = UUID.randomUUID() + suffix;
        }
        try {
            log.info("上传文件名 ：{}",fileName);
            byte[] bytes = file.getBytes();
            QiNiuUtil.uploadToQiNiu(accessKey,secretKey,bucket,fileName,bytes);
        }catch (Exception e){
            log.info("上传失败 Exception： {}",e.getMessage());
            throw new BusinessException(CommonErrorCode.E_100106);
        }
        String  ap = url + fileName;
        log.info("上传成功 据对路径：{}",ap);
        return ap;
    }
}
