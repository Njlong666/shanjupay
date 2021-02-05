package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/*****
 *@Author NJL
 *@Description MerchantConvert转化类
 */
@Mapper
public interface MerchantConvertController {

    MerchantConvertController INSTANCE  =  Mappers.getMapper(MerchantConvertController.class);

    /***
     * dto转换为VO
     * @param merchantDTO  dto
     * @return vo
     */
    MerchantRegisterVO dtoToVo (MerchantDTO merchantDTO);

    /***
     * vo转换为dto
     * @param merchantRegisterVO  dto
     * @return vo
     */
    MerchantDTO voToDto (MerchantRegisterVO merchantRegisterVO);

    /***
     * voList转换为DTOList
     * @param merchantRegisterVOList  voList
     * @return DTOList
     */
    List<MerchantDTO> voListToDtoList (List<MerchantRegisterVO> merchantRegisterVOList);

    /***
     * DtoList转换为VoList
     * @param merchantDTOList  dtoList
     * @return VoList
     */
    List<MerchantRegisterVO> dtoListToVoList (List<MerchantDTO> merchantDTOList);

    public static void main(String[] args) {
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(1231231231223L);
        merchantDTO.setUsername("1231313213");
        merchantDTO.setAuditStatus("1");
        MerchantRegisterVO merchantRegisterVO  = MerchantConvertController.INSTANCE.dtoToVo(merchantDTO);
        System.out.println(merchantRegisterVO);
    }


}
