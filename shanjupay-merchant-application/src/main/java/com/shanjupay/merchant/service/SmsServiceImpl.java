package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*****
 *@Author NJL
 *@Description 验证码服务
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Resource
    private RestTemplate restTemplate;

    /***
     * 验证码服务URL
     */
    @Value("${CaptchaService.url}")
    private String captchaServiceUrl;

    /***
     * 发送验证码
     * @param phone 手机号
     * @return key
     */
    @Override
    public Map<String,Object> generate(String phone) throws BusinessException {
        String url =  captchaServiceUrl + "generate?effectiveTime=600&name=sms";
        HttpHeaders tempHeaders = new HttpHeaders();
        tempHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> body = new HashMap<>();
        body.put("mobile",phone);

        HttpEntity httpEntity = new HttpEntity(body,tempHeaders);
        log.info("请求验证码接口参数 httpEntity：{}",httpEntity);
        Map responseMap  = null;
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
            responseMap  = exchange.getBody();
        }catch (Exception e){
            throw new RuntimeException("调用验证码服务错误");
        }
        log.info("掉用验证码接口返回参数：{}",responseMap);

        return responseMap;
    }

    /***
     * 校验验证码
     * @param verificationCode 验证码
     * @param verificationKey 验证码key
     */
    @Override
    public void verify(String verificationCode, String verificationKey) throws BusinessException{
        String url = captchaServiceUrl + "verify?name=sms&verificationCode="+ verificationCode +"&verificationKey=" + verificationKey;
        log.info("请求验证码接口参数 URL：{}",url);
        Map responseMap  = null;
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            responseMap  = exchange.getBody();
        }catch (Exception e){
            throw new BusinessException(CommonErrorCode.E_100102);
        }
        log.info("掉用验证码接口返回参数：{}",responseMap);
        if (responseMap == null || responseMap.get("result") == null || !(Boolean) responseMap.get("result")){
            throw new BusinessException(CommonErrorCode.E_100102);
        }

    }
}
