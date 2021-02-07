package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.AppDTO;

import java.util.List;

/*****
 *@Author NJL
 *@Description 商户应用
 */
public interface AppService {

    /***
     * 商户下创建应用
     * @param merchantId  merchantId
     * @param app 商户应用信息
     * @return AppDTO
     * @throws BusinessException
     */
    AppDTO createApp(Long merchantId, AppDTO app) throws BusinessException;


    /***
     * 根据appId查询应用信息
     * @param appId 应用ID
     * @return AppDTO
     */
    AppDTO queryByAppId(String appId);


    /***
     * 根据商户ID查询应用列表
     * @param  merchantId  merchantId
     * @return  List<AppDTO>
     */
    List<AppDTO> queryByMerchantId(Long merchantId);
}
