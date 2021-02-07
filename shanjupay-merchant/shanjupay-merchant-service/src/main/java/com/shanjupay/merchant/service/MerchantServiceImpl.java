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
import org.springframework.transaction.annotation.Transactional;

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
            throw new BusinessException(CommonErrorCode.E_110006);
        }

        if (StringUtil.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }

        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }

        //查询手机号是否已经存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        return true;
    }



    /***
     * 商户资质申请
     * @param merchantId 商户ID
     * @param merchantDTO 商户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void qualificationApplyFor(Long merchantId, MerchantDTO merchantDTO) throws BusinessException{
        Merchant merchant = qualificationApplyForCheckParam(merchantId, merchantDTO);
        if (Objects.nonNull(merchant)){
            Merchant merchantRequest = MerchantConvert.INSTANCE.dtoToEntity(merchantDTO);
            if (Objects.nonNull(merchant.getMobile())){
                merchantRequest.setMobile(merchant.getMobile());
            }
            if (Objects.nonNull(merchant.getTenantId())){
                merchantRequest.setTenantId(merchant.getTenantId());
            }
            if (Objects.nonNull(merchant.getUsername())){
                merchantRequest.setUsername(merchant.getUsername());
            }
            merchantRequest.setId(merchant.getId());
            //1-已申请待审核
            merchantRequest.setAuditStatus("1");
            merchantMapper.updateById(merchantRequest);
        }
    }

    /***
     * 商户资质申请校验参数
     * @param merchantDTO 商户信息
     * @return 参数无误返回true
     * @throws BusinessException
     */
    private Merchant qualificationApplyForCheckParam(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {

        if (Objects.isNull(merchantDTO)) {
            throw new BusinessException(CommonErrorCode.E_110006);
        }
        if (Objects.isNull(merchantId)) {
            throw new BusinessException(CommonErrorCode.E_110006);
        }

        Merchant merchant = merchantMapper.selectById(merchantId);
        if (Objects.isNull(merchant)){
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        return merchant;
    }




    /***
     *  商户资质审核
     * @param merchantId 商户ID
     * @param auditStatus 商户资质审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
     * @return MerchantDTO
     * @throws BusinessException
     */
    @Override
    public MerchantDTO updateMerchantAuditStatus(Long merchantId, String auditStatus) throws BusinessException {

        if (Objects.isNull(merchantId) || StringUtil.isBlank(auditStatus)){
            throw new BusinessException(CommonErrorCode.E_110006);
        }

        Merchant merchant = new Merchant();
        merchant.setId(merchantId);
        merchant.setAuditStatus(auditStatus);

        merchantMapper.updateById(merchant);

        return MerchantConvert.INSTANCE.entityToDto(merchant);
    }

}
