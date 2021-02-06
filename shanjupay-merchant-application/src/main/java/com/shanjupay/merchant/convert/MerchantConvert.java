package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/*****
 *@Author NJL
 *@Description MerchantConvert转化类
 */
@Mapper
public interface MerchantConvert {

    MerchantConvert INSTANCE  =  Mappers.getMapper(MerchantConvert.class);

    /***
     * dto转换为VO
     * @param merchantDTO  dto
     * @return vo
     */
    MerchantDetailVO dtoToVo(MerchantDTO merchantDTO);

    /***
     * vo转换为dto
     * @param merchantDetailVO  dto
     * @return vo
     */
    MerchantDTO voToDto(MerchantDetailVO merchantDetailVO);

    /***
     * voList转换为DTOList
     * @param merchantDetailVOList  voList
     * @return DTOList
     */
    List<MerchantDTO> voListToDtoList(List<MerchantDetailVO> merchantDetailVOList);

    /***
     * DtoList转换为VoList
     * @param merchantDTOList  dtoList
     * @return VoList
     */
    List<MerchantDetailVO> dtoListToVoList(List<MerchantDTO> merchantDTOList);


}
