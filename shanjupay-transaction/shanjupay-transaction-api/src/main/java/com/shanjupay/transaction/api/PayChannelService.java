package com.shanjupay.transaction.api;

import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/*****
 *@Author NJL
 *@Description 支付渠道服务
 */
public interface PayChannelService {


    /***
     * 查询所有支付渠道
     * @return
     */
    List<PlatformChannelDTO> queryAllPayChannel();
}
