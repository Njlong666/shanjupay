package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.common.util.StringUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/*****
 *@Author NJL
 *@Description MerchantServiceImpl
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Resource
    MerchantMapper merchantMapper;

    @Resource
    StoreMapper storeMapper;

    @Resource
    StaffMapper staffMapper;

    @Resource
    StoreStaffMapper storeStaffMapper;

    @Reference
    TenantService tenantService;


    /**
     * 根据ID查询详细信息
     *
     * @param merchantId
     * @return @throws BusinessException
     */
    @Override
    public MerchantDTO queryMerchantById(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
//        Long id = merchant.getId();
//        MerchantDTO merchantDTO = new MerchantDTO();
//
//        merchantDTO.setId(id);

        return MerchantConvert.INSTANCE.entityToDto(merchant);
    }

    /***
     * 保存商户信息
     * @param merchantDTO 保存的商户信息
     * @return 返回保存的商户信息
     */
    @Override
    @Transactional
    public MerchantDTO saveMerchant(MerchantDTO merchantDTO) throws BusinessException {
        //参数校验
        saveMerchantCheckParam(merchantDTO);

        //添加租户 和账号 并绑定关系
        CreateTenantRequestDTO createTenantRequestDTO = new CreateTenantRequestDTO();
        createTenantRequestDTO.setMobile(merchantDTO.getMobile());
        createTenantRequestDTO.setUsername(merchantDTO.getUsername());
        createTenantRequestDTO.setPassword(merchantDTO.getPassword());
        createTenantRequestDTO.setName(merchantDTO.getUsername());
        //表示该租户类型是商户
        createTenantRequestDTO.setTenantTypeCode("shanju‐merchant");
        //设置租户套餐为初始化套餐餐
        createTenantRequestDTO.setBundleCode("shanju‐merchant");
        TenantDTO tenantAndAccount = tenantService.createTenantAndAccount(createTenantRequestDTO);
        if (Objects.isNull(tenantAndAccount) || Objects.isNull(tenantAndAccount.getId())){
            throw new BusinessException(CommonErrorCode.E_200012);
        }
        //判断租户下是否已经注册过商户
        Merchant merchantEntity = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantAndAccount.getId()));
        if (Objects.nonNull(merchantEntity)){
            throw new BusinessException(CommonErrorCode.E_200017);
        }

        Merchant merchant = MerchantConvert.INSTANCE.dtoToEntity(merchantDTO);
        //审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
        merchant.setAuditStatus("0");
        merchant.setTenantId(tenantAndAccount.getId());
        merchantMapper.insert(merchant);

        //新增门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchant.getId());
        storeDTO.setStoreName("根门店");
        storeDTO.setStoreStatus(true);
        StoreDTO store = createStore(storeDTO);

        //新增员工
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMobile(merchantDTO.getMobile());
        staffDTO.setUsername(merchantDTO.getUsername());
        staffDTO.setStoreId(store.getId());
        staffDTO.setMerchantId(merchant.getId());
        staffDTO.setStaffStatus(true);
        StaffDTO staff = createStaff(staffDTO);

        //新增员工和员工关联关系
        bindStaffToStore(store.getId(),staff.getId());


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

        if (Objects.isNull(merchantDTO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }

        //查询手机号是否已经存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile, merchantDTO.getMobile()));
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

    /***
     * 保存门店信息
     * @param storeDTO storeDTO
     * @return storeDTO
     * @throws BusinessException
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        if (Objects.isNull(storeDTO)){
            throw new BusinessException(CommonErrorCode.E_110006);
        }
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        storeMapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }


    /***
     * 保存员工信息
     * @param staffDTO 员工信息
     * @return StaffDTO
     * @throws BusinessException
     */
    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {
        if (Objects.isNull(staffDTO) || Objects.isNull(staffDTO.getMobile())
                                     || Objects.isNull(staffDTO.getUsername())
                                     || Objects.isNull(staffDTO.getMerchantId())){
            throw new BusinessException(CommonErrorCode.E_110006);
        }
        Boolean existStaffByMobile = isExistStaffByMobile(staffDTO.getMobile(), staffDTO.getMerchantId());
        if (existStaffByMobile){
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        Boolean existStaffByUserName = isExistStaffByUserName(staffDTO.getUsername(), staffDTO.getMerchantId());
        if (existStaffByUserName){
            throw new BusinessException(CommonErrorCode.E_100114);
        }

        Staff staff = StaffConvert.INSTANCE.dto2entity(staffDTO);
        staffMapper.insert(staff);

        return StaffConvert.INSTANCE.entity2dto(staff);
    }

    /***
     * 保存门店与员工关联信息
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        if (Objects.isNull(staffId) || Objects.isNull(storeId)){
            throw new BusinessException(CommonErrorCode.E_110006);
        }
        StoreStaff  storeStaff = new StoreStaff();
        storeStaff.setStaffId(staffId);
        storeStaff.setStoreId(storeId);
        storeStaffMapper.insert(storeStaff);
    }

    /***
     * 手机号是否唯一
     * @param mobile 手机号
     * @param merchantId 商户ID
     * @return 默认true
     */
    private Boolean isExistStaffByMobile(String mobile, Long merchantId){
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>().eq(Staff::getMobile, mobile)
                .eq(Staff::getMerchantId,merchantId));
        return count > 0;
    }


    /***
     * 账户是否唯一
     * @param userName 账户是否唯一
     * @param merchantId 商户ID
     * @return 默认true
     */
    private Boolean isExistStaffByUserName(String userName, Long merchantId){
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>().eq(Staff::getUsername, userName)
                .eq(Staff::getMerchantId,merchantId));
        return count > 0;
    }





    /***
     * 查询租户下的商户
     * @param tenantId 租户ID
     * @return MerchantDTO
     * @throws BusinessException
     */
    @Override
    public MerchantDTO queryMerchantByTenantId(Long tenantId) throws BusinessException {
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantId));
        return MerchantConvert.INSTANCE.entityToDto(merchant);
    }

}
