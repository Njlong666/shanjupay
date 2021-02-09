package com.shanjupay.merchant.controller;

import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*****
 *@Author NJL
 *@Description 商户平台‐渠道和支付参数Controller
 */

@Api(value = "商户平台‐渠道和支付参数相关", tags = "商户平台‐渠道和支付参数", description = "商户平 台‐渠道和支付参数相关")
@RestController
public class PlatformParamController {


    @Reference
    private PayChannelService payChannelService;


    @ApiOperation("获取平台服务类型")
    @GetMapping(value = "/my/platform‐channels")
    public List<PlatformChannelDTO> queryPlatformChannel() {
        return payChannelService.queryAllPayChannel();
    }


}
