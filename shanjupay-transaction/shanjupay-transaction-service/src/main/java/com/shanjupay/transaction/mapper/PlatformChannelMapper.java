package com.shanjupay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.entity.PlatformChannel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2019-11-15
 */
@Repository
public interface PlatformChannelMapper extends BaseMapper<PlatformChannel> {


    /***
     * 根据平台服务类型获取原始支付渠道
     * @param platformChannelCode 支付渠道编码
     * @return 支付渠道列表
     */
    public List<PayChannelDTO> selectPayChannelByPlatformChannel(String platformChannelCode);
}
