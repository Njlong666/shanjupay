package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/*****
 *@Author NJL
 *@Description MerchantServiceImpl
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    /**
     * 根据ID查询详细信息
     *
     * @param merchantId
     * @return @throws BusinessException
     */
    @Override
    public MerchantDTO queryMerchantById(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Long id = merchant.getId();
        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setId(id);

        return merchantDTO;
    }
}
