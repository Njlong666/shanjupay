package com.shanjupay.merchant.api;

import com.shanjupay.merchant.api.dto.MerchantDTO;

/*****
 *@Author NJL
 *@Description MerchantService
 */
public interface MerchantService {
    /**
     * 根据ID查询详细信息 * @param merchantId * @return * @throws BusinessException
     */
    MerchantDTO queryMerchantById(Long merchantId);
}
