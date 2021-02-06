package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
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


    /***
     * 保存商户信息
     * @param merchantDTO 保存的商户信息
     * @return 返回保存的商户信息
     */
    public MerchantDTO saveMerchant(MerchantDTO merchantDTO) throws BusinessException;
}
