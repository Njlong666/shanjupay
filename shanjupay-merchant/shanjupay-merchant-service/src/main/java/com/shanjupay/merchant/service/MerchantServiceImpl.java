package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
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

    /***
     * 保存商户信息
     * @param merchantDTO 保存的商户信息
     * @return 返回保存的商户信息
     */
    @Override
    public MerchantDTO saveMerchant(MerchantDTO merchantDTO) {
        Merchant merchant = MerchantConvert.INSTANCE.dtoToEntity(merchantDTO);
        //审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
        merchant.setAuditStatus("0");
        merchantMapper.insert(merchant);

        MerchantDTO merchantDtoResp = MerchantConvert.INSTANCE.entityToDto(merchant);
        merchantDtoResp.setId(merchant.getId());
        return merchantDtoResp;
    }
}
