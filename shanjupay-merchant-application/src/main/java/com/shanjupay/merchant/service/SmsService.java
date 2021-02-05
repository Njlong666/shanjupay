package com.shanjupay.merchant.service;

import java.util.Map;

/*****
 *@Author NJL
 *@Description 验证码
 */
public interface SmsService {

    /***
     * 发送验证码
     * @param phone 手机号
     * @return key
     */
    Map<String, Object> generate(String phone);


    /***
     * 校验验证码
     * @param verificationCode 验证码
     * @param verificationKey 验证码key
     */
    void verify(String verificationCode,String verificationKey);


}
