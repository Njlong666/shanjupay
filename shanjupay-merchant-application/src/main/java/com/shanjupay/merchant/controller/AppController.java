package com.shanjupay.merchant.controller;

import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.PayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*****
 *@Author NJL
 *@Description 应用管理
 */

@Api(value = "商户平台‐应用管理", tags = "商户平台‐应用相关", description = "商户平台‐应用相关")
@RestController
public class AppController {

    @Reference
    private AppService appService;

    @Reference
    private PayChannelService payChannelService;


    @ApiOperation("商户创建应用")
    @ApiImplicitParams({@ApiImplicitParam(name = "app", value = "应用信息", required = true, dataType = "AppDTO", paramType = "body")})
    @PostMapping(value = "/my/apps")
    public AppDTO createApp(@RequestBody AppDTO app) {
        Long merchantId = SecurityUtil.getMerchantId();
        return appService.createApp(merchantId, app);
    }


    /***
     * 查询商户下的应用列表
     * @return List<AppDTO>
     */
    @ApiOperation("查询商户下的应用列表")
    @GetMapping(value = "/my/apps")
    public List<AppDTO> queryMyApps() {
        Long merchantId = SecurityUtil.getMerchantId();
        return appService.queryByMerchantId(merchantId);
    }


    /***
     * 根据appid获取应用的详细信息
     * @param appId appId
     * @return AppDTO
     */
    @ApiOperation("根据appid获取应用的详细信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "appId", value = "商户应用id", required = true, dataType = "String", paramType = "path")})
    @GetMapping(value = "/my/apps/{appId}")
    public AppDTO getApp(@PathVariable String appId) {
        return appService.queryByAppId(appId);
    }


    @ApiOperation("绑定服务类型")
    @PostMapping(value = "/my/apps/{appId}/platform‐channels")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用id", name = "appId", dataType = "string", paramType = "path"),
            @ApiImplicitParam(value = "服务类型code", name = "platformChannelCodes", dataType = "string", paramType = "query")})
    public void bindPlatformForApp(@PathVariable("appId") String appId, @RequestParam("platformChannelCodes") String platformChannelCodes) {
        payChannelService.bindPlatformChannelForApp(appId, platformChannelCodes);
    }
}
