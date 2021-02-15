package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;

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
     *  保存商户信息
     * @param merchantDTO 保存的商户信息
     * @return 返回保存的商户信息
     * @throws BusinessException
     */
     MerchantDTO saveMerchant(MerchantDTO merchantDTO) throws BusinessException;


    /***
     * 商户资质申请
     * @param merchantId 商户ID
     * @param merchantDTO 商户信息
     * @throws BusinessException
     */
    void qualificationApplyFor(Long merchantId ,MerchantDTO merchantDTO) throws BusinessException;


    /***
     *  商户资质审核
     * @param merchantId 商户ID
     * @param auditStatus 商户资质审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
     * @return MerchantDTO
     * @throws BusinessException
     */
    MerchantDTO updateMerchantAuditStatus(Long merchantId,String auditStatus)throws BusinessException;


    /***
     * 保存门店信息
     * @param storeDTO storeDTO
     * @return storeDTO
     * @throws BusinessException
     */
    StoreDTO createStore(StoreDTO storeDTO)throws BusinessException;


    /***
     * 保存员工信息
     * @param staffDTO 员工信息
     * @return StaffDTO
     * @throws BusinessException
     */
    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;


    /***
     * 保存门店与员工关联信息
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    void bindStaffToStore(Long storeId , Long staffId) throws BusinessException;

}
