package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
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
     * entity转换为DTO
     * @param merchant  entity
     * @return DTO
     */
    MerchantDTO entityToDto (Merchant merchant);

    /***
     * DTO转换为Entity
     * @param merchantDTO
     * @return dto
     */
    Merchant dtoToEntity(MerchantDTO merchantDTO);

    /***
     * entityList转换为DTOList
     * @param merchantList  entityList
     * @return DTOList
     */
    List<MerchantDTO> entityListToDtoList (List<Merchant> merchantList);

    /***
     * DTOList转换为entityList
     * @param merchantDtoList  dtoList
     * @return entityList
     */
    List<Merchant> dtoListToEntityList (List<MerchantDTO> merchantDtoList);


}
