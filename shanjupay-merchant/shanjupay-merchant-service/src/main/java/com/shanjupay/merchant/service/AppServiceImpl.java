package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.convert.AppCovert;
import com.shanjupay.merchant.entity.App;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.AppMapper;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/*****
 *@Author NJL
 *@Description 商户应用实现
 */

@Service
public class AppServiceImpl implements AppService {

    @Resource
    AppMapper appMapper;

    @Resource
    MerchantMapper merchantMapper;


    /***
     * 商户下创建应用
     * @param merchantId  merchantId
     * @param app 商户应用信息
     * @return AppDTO
     * @throws BusinessException
     */
    @Override
    public AppDTO createApp(Long merchantId, AppDTO app) throws BusinessException {

        /***
         * 1、接口描述
         * 1）校验商户是否通过资质审核 如果商户资质审核没有通过不允许创建应用。
         * 2）生成应用ID 应用Id使用UUID方式生成。
         * 3）保存商户应用信息 应用名称需要校验唯一性。
         */
        if (createAppCheckParam(merchantId, app)) {
            String appId = UUID.randomUUID() + "";
            App entity = AppCovert.INSTANCE.dto2entity(app);
            entity.setAppId(appId);
            entity.setMerchantId(merchantId);
            appMapper.insert(entity);

            return AppCovert.INSTANCE.entity2dto(entity);
        }
        return null;
    }


    /***
     * 商户下创建应用参数校验
     * @param merchantId merchantId
     * @param app app
     * @return true
     */
    private Boolean createAppCheckParam(Long merchantId, AppDTO app) {
        if (Objects.isNull(merchantId) || Objects.isNull(app) || Objects.isNull(app.getAppName())) {
            throw new BusinessException(CommonErrorCode.E_110006);
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (Objects.isNull(merchant)) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        if (!"2".equals(merchant.getAuditStatus())) {
            throw new BusinessException(CommonErrorCode.E_200003);
        }

        Integer count = appMapper.selectCount(new LambdaQueryWrapper<App>().eq(App::getAppName, app.getAppName()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_200004);
        }
        return true;
    }


    /***
     * 根据appId查询应用信息
     * @param appId 应用ID
     * @return AppDTO
     */
    @Override
    public AppDTO queryByAppId(String appId) {
        App app = appMapper.selectOne(new LambdaQueryWrapper<App>().eq(App::getAppId, appId));
        return AppCovert.INSTANCE.entity2dto(app);
    }

    /***
     * 根据商户ID查询应用列表
     * @param  merchantId  merchantId
     * @return List<AppDTO>
     */
    @Override
    public List<AppDTO> queryByMerchantId(Long merchantId) {
        List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getMerchantId, merchantId));
        return AppCovert.INSTANCE.listentity2dto(apps);
    }


}
