package com.shanjupay.merchant.controller;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantConvertController;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.Map;

/*****
 *@Author NJL
 *@Description MerchantController
 */

@Api(value = "商户平台‐商户相关", tags = "商户平台‐商户相关", description = "商户平台‐商户相关")
@RestController
public class MerchantController {

    @Reference
    private MerchantService merchantService;

    @Resource
    private SmsService smsService;

    @ApiOperation("根据id查询商户")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id) {
        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }

    @ApiOperation("测试")
    @GetMapping(path = "/hello")
    public String hello() {
        return "hello";
    }

    @ApiOperation("测试")
    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "string")
    @PostMapping(value = "/hi")
    public String hi(String name) {
        return "hi," + name;
    }


    /***
     * 获取验证码
     * @param phone 手机号
     * @return key
     */
    @ApiOperation("获取验证码")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "string")
    @PostMapping(value = "/merchant/generate")
    public Map<String, Object> generate(@RequestParam("phone") String phone) {
        return smsService.generate(phone);
    }



    /***
     * 商户注册
     * @param merchantRegisterVO 商户注册信息
     * @return 商户注册信息
     */
    @ApiOperation("商户注册")
    @ApiImplicitParam(name = "merchantRegisterVO", value = "商户注册信息", required = true, dataType = "body")
    @PostMapping(value = "/merchant/registration")
    public MerchantRegisterVO registration(@RequestBody  MerchantRegisterVO  merchantRegisterVO) {


        smsService.verify(merchantRegisterVO.getVerifiyCode(),merchantRegisterVO.getVerifiykey());


        MerchantDTO merchantDTO = MerchantConvertController.INSTANCE.voToDto(merchantRegisterVO);
        merchantService.saveMerchant(merchantDTO);

        return MerchantConvertController.INSTANCE.dtoToVo(merchantDTO);
    }


}
