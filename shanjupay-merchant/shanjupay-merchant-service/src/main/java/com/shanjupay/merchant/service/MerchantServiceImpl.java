package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.common.util.StringUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Objects;

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
    public MerchantDTO saveMerchant(MerchantDTO merchantDTO) throws BusinessException {
        //参数校验
        saveMerchantCheckParam(merchantDTO);

        Merchant merchant = MerchantConvert.INSTANCE.dtoToEntity(merchantDTO);
        //审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
        merchant.setAuditStatus("0");
        merchantMapper.insert(merchant);

        MerchantDTO merchantDtoResp = MerchantConvert.INSTANCE.entityToDto(merchant);
        merchantDtoResp.setId(merchant.getId());
        return merchantDtoResp;
    }

    /***
     * 校验参数
     * @param merchantDTO merchantDTO
     * @return 参数无误返回true
     */
    private Boolean saveMerchantCheckParam(MerchantDTO merchantDTO) throws BusinessException {

        if (Objects.isNull(merchantDTO)) {
            throw new BusinessException(CommonErrorCode.E_200201);
        }

        if (StringUtil.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_200230);
        }

        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_200224);
        }

        //查询手机号是否已经存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_200203);
        }
        return true;
    }
}
