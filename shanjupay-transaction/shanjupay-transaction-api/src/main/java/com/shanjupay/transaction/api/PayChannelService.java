package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
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


    /***
     *
     * 用是否已经绑定服务类型
     * @param appId  应用ID
     * @param platformChannel 平台服务类型Code
     * @return   已绑定返回1，否则 返回0
     * @throws BusinessException
     */
    int queryAppBindPlatformChannel(String appId,String platformChannel) throws BusinessException;


    /***
     * 根据平台服务类型获取原始支付渠道
     * @param platformChannelCode 支付渠道编码
     * @return 支付渠道列表
     */
    List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode);


    /***
     * 保存支付渠道参数
     * @param payChannelParam 支付渠道参数
     * @throws BusinessException
     */
    void savePayChannelParam(PayChannelParamDTO payChannelParam) throws BusinessException;
}
