package com.shanjupay.common.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/*****
 *@Author NJL
 *@Description 错误响应包装类
 */

@ApiModel(value = "RestErrorResponse", description = "错误响应参数包装")
@Data
public class RestErrorResponse {


    private int errCode;

    private String errMessage;

    public RestErrorResponse(int errCode, String errMessage) {
        this.errCode = errCode;
        this.errMessage = errMessage;
    }
}
