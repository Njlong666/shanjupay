package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/*****
 *@Author NJL
 *@Description 支付渠道服务
 */
public interface PayChannelService {


    /***
     * 查询所有支付渠道
     * @return List<PlatformChannelDTO>
     */
    List<PlatformChannelDTO> queryAllPayChannel();


    /***
     * 绑定定平台服务类型
     * @param appId 应用ID
     * @param platformChannelCodes 平台服务类型列表
     * @throws BusinessException
     */
    void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException;
}
