package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.cache.Cache;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RedisUtil;
import com.shanjupay.common.util.StringUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PayChannelParamConvert;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelParamMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/*****
 *@Author NJL
 *@Description 支付渠道服务实现
 */


@Service
public class PayChannelServiceImpl implements PayChannelService {

    @Resource
    private PlatformChannelMapper platformChannelMapper;

    @Resource
    private AppPlatformChannelMapper appPlatformChannelMapper;

    @Resource
    private PayChannelParamMapper payChannelParamMapper;

    @Resource
    private Cache cache;

    /***
     * 查询所有支付渠道
     * @return
     */
    @Override
    public List<PlatformChannelDTO> queryAllPayChannel() {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        return PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
    }

    /***
     * 绑定定平台服务类型
     * @param appId 应用ID
     * @param platformChannelCodes 平台服务类型列表Code
     * @throws BusinessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException {
        checkParam(appId,platformChannelCodes);

        AppPlatformChannel entity = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes));
        if (Objects.isNull(entity)){

            AppPlatformChannel appPlatformChannel = new AppPlatformChannel();
            appPlatformChannel.setAppId(appId);
            appPlatformChannel.setPlatformChannel(platformChannelCodes);
            appPlatformChannelMapper.insert(appPlatformChannel);
        }

    }

    /***\
     *
     * 用是否已经绑定服务类型
     * @param appId  应用ID
     * @param platformChannel 平台服务类型Code
     * @return 已绑定返回1，否则 返回0
     * @throws BusinessException
     */
    @Override
    public int queryAppBindPlatformChannel(String appId, String platformChannel) throws BusinessException {
        AppPlatformChannel entity = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannel));
        if (Objects.nonNull(entity)){
            return 1;
        }
        return 0;
    }


    /***
     *  bindPlatformChannelForApp方法参数校验
     * @param appId 应用ID
     * @param platformChannelCodes 平台服务类型列表Code
     * @return
     */
    private void checkParam(String appId, String platformChannelCodes) throws BusinessException{
        if (StringUtil.isBlank(appId) || StringUtil.isBlank(platformChannelCodes)){
            throw new BusinessException(CommonErrorCode.E_110006);
        }
    }



    /***
     * 根据平台服务类型获取原始支付渠道
     * @param platformChannelCode 支付渠道编码
     * @return 支付渠道列表
     */
    @Override
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode) {
        return platformChannelMapper.selectPayChannelByPlatformChannel(platformChannelCode);
    }


    /***
     * 保存支付渠道参数
     * @param payChannelParam 支付渠道参数
     * @throws BusinessException
     */
    @Override
    public void savePayChannelParam(PayChannelParamDTO payChannelParam) throws BusinessException {
        if (Objects.isNull(payChannelParam) || Objects.isNull(payChannelParam.getAppId())
                                            || Objects.isNull(payChannelParam.getPlatformChannelCode())
                                            || Objects.isNull(payChannelParam.getPayChannel())){
            throw new BusinessException(CommonErrorCode.E_110006);
        }

        /*
        * 该应用的服务类型已经配置某支付渠道参数则执行更新操作，
        * 否 执行添加操作
        * */
        Long appPlatformChannelId = selectIdByAppPlatformChannel(payChannelParam.getAppId(),
                payChannelParam.getPlatformChannelCode());
        if (Objects.isNull(appPlatformChannelId)){
            throw new BusinessException(CommonErrorCode.E_300010);
        }
        PayChannelParam payChannelEntity = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId)
                .eq(PayChannelParam::getPayChannel, payChannelParam.getPayChannel()));
        if (Objects.nonNull(payChannelEntity)){
            payChannelEntity.setChannelName(payChannelParam.getChannelName());
            payChannelEntity.setParam(payChannelParam.getParam());
            payChannelParamMapper.updateById(payChannelEntity);
        }else {
            PayChannelParam dto2entity = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParam);
            dto2entity.setAppPlatformChannelId(appPlatformChannelId);
            payChannelParamMapper.insert(dto2entity);
        }

        updateCache(payChannelParam.getAppId(),payChannelParam.getPlatformChannelCode());
    }

    /***
     * 查询支付渠道参数列表
     * @param appId appId
     * @param platformChannelCode platformChannelCode
     * @return 支付渠道参数列表
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String platformChannelCode) {
        String key = RedisUtil.keyBuilder(appId, platformChannelCode);
        boolean exists = cache.exists(key);
        if (exists){
            String redisCache = cache.get(key);
            if (!StringUtil.isBlank(redisCache)){
                List<PayChannelParam> payChannelParams = JSON.parseArray(redisCache, PayChannelParam.class);
                return PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
            }
        }

        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannelCode);
        if (Objects.nonNull(appPlatformChannelId)){
            List<PayChannelParam> payChannelParamList = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>()
                    .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
            if (!payChannelParamList.isEmpty()){
                cache.set(key, JSON.toJSONString(payChannelParamList));
            }
            return PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParamList);
        }
        return null;
    }

    /***
     * 支付渠道参数详细信息
     * @param appId appId
     * @param platformChannelCode platformChannelCode
     * @param payChannel payChannel
     * @return 详细信息comm
     */
    @Override
    public PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannelCode, String payChannel) {
        List<PayChannelParamDTO> payChannelParamDtoList= queryPayChannelParamByAppAndPlatform(appId, platformChannelCode);
        if (!payChannelParamDtoList.isEmpty()){
            for (PayChannelParamDTO payChannelParamDTO : payChannelParamDtoList) {
                if (payChannelParamDTO.getPayChannel().equals(payChannel)){
                    return payChannelParamDTO;
                }
            }
        }
        return null;
    }


    /***
     * 根据AppId platformChannelCode 查询
     * @param appId 应用ID
     * @param platformChannelCode 应用绑定的服务类型对应的code
     * @return  应用绑定的服务类型Id
     */
    private Long selectIdByAppPlatformChannel(String appId,String platformChannelCode){
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));
        if (Objects.nonNull(appPlatformChannel)){
            return appPlatformChannel.getId();
        }
        return null;
    }


    /***
     * 保存缓存
     * @param appId 应用ID
     * @param platformChannelCode 服务类型编码
     */
    private void updateCache(String appId,String platformChannelCode){
        String key = RedisUtil.keyBuilder(appId, platformChannelCode);
        boolean exists = cache.exists(key);
        if (exists){
            cache.del(key);
        }
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannelCode);
        if (Objects.nonNull(appPlatformChannelId)){
            List<PayChannelParam> payChannelParamList = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>()
                    .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
           // return PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParamList);
            if (!payChannelParamList.isEmpty()){
                cache.set(key, JSON.toJSONString(payChannelParamList));
            }
        }
    }

}
