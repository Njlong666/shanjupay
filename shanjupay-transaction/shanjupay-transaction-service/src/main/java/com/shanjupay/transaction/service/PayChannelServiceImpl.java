package com.shanjupay.transaction.service;

import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/*****
 *@Author NJL
 *@Description 支付渠道服务实现
 */


@Service
public class PayChannelServiceImpl implements PayChannelService {

    @Resource
    private PlatformChannelMapper platformChannelMapper;


    /***
     * 查询所有支付渠道
     * @return
     */
    @Override
    public List<PlatformChannelDTO> queryAllPayChannel() {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        return PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
    }
}
