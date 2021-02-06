package com.shanjupay.merchant.service;

import org.springframework.web.multipart.MultipartFile;

/*****
 *@Author NJL
 *@Description 文件上传
 */
public interface FileService {

    /****
     * 证件照上传
     * @param file 证件照
     * @return 绝对路径
     */
    String upload( MultipartFile file);
}
