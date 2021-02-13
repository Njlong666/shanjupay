package com.shanjupay.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.StringUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
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

}
