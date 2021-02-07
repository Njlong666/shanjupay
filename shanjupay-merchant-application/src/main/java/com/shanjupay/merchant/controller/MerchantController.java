package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.MerchantConvertController;
import com.shanjupay.merchant.service.FileService;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

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

    @Resource
    private FileService fileService;


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
    @PostMapping(value = "/generate")
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
    @PostMapping(value = "/registration")
    public MerchantRegisterVO registration(@RequestBody  MerchantRegisterVO  merchantRegisterVO) {


        smsService.verify(merchantRegisterVO.getVerifiyCode(),merchantRegisterVO.getVerifiykey());


        MerchantDTO merchantDTO = MerchantConvertController.INSTANCE.voToDto(merchantRegisterVO);
        merchantService.saveMerchant(merchantDTO);

        return MerchantConvertController.INSTANCE.dtoToVo(merchantDTO);
    }


    /****
     * 证件照上传
     * @param file 证件照
     * @return 绝对路径
     */
    @ApiOperation("证件照上传")
    @PostMapping(value = "/upload")
    public String upload( @ApiParam(value = "证件照",required = true) @RequestParam("file") MultipartFile file){
        return fileService.upload(file);
    }


    /***
     * 商户资质申请
     * @param merchantInfo  merchantInfo
     */
    @ApiOperation("商户资质申请")
    @ApiImplicitParams({ @ApiImplicitParam(name = "merchantInfo", value = "商户认证资料", required = true, dataType = "MerchantDetailVO", paramType = "body") })
    @PostMapping("/my/merchants/save")
    public void saveMerchant(@RequestBody MerchantDetailVO merchantInfo) {
        //token中获取用户信息
        Long merchantId = SecurityUtil.getMerchantId();
        if (Objects.isNull(merchantId)){
            throw new BusinessException(CommonErrorCode.E_200018);
        }
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.voToDto(merchantInfo);
        merchantService.qualificationApplyFor(merchantId,merchantDTO);
    }



    /***
     * 商户资质审核
     * @param auditStatus  审核状态 2-审核通过,3-审核拒绝
     */
    @ApiOperation("商户资质审核")
    @ApiImplicitParams({ @ApiImplicitParam(name = "auditStatus", value = "商户资质审核", required = true, dataType = "String") })
    @PostMapping("/my/merchants/updateMerchantAuditStatus")
    public MerchantDTO updateMerchantAuditStatus( @ApiParam(name = "auditStatus",value = "审核状态 2-审核通过,3-审核拒绝",required = true)
                                                      @RequestParam("auditStatus") String auditStatus){
        //token中获取用户信息
        Long merchantId = SecurityUtil.getMerchantId();
        if (Objects.isNull(merchantId)){
            throw new BusinessException(CommonErrorCode.E_200018);
        }
        return merchantService.updateMerchantAuditStatus(merchantId, auditStatus);
    }

}
