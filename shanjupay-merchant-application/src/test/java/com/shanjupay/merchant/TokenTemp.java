package com.shanjupay.merchant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/*****
 *@Author NJL
 *@Description TODO
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenTemp {

    @Resource
    MerchantService merchantService;

    @Test
    public void createTestToken(){
        Long merchantId = 1357932761967476737L;//填写用于测试的商户id Bearer eyJtZXJjaGFudElkIjoxMzU3OTMyNzYxOTY3NDc2NzM3fQ==
        MerchantDTO merchantDTO = merchantService.queryMerchantById(merchantId);
        JSONObject token = new JSONObject();
        token.put("mobile", merchantDTO.getMobile());
        token.put("user_name", merchantDTO.getUsername());
        token.put("merchantId", merchantId);
        String jwt_token = "Bearer " + EncryptUtil.encodeBase64(JSON.toJSONString(token).getBytes());
        System.out.println(jwt_token);
    }




}
