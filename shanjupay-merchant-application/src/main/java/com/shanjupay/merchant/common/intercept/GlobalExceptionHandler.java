package com.shanjupay.merchant.common.intercept;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.ErrorCode;
import com.shanjupay.common.domain.RestErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*****
 *@Author NJL
 *@Description 全局统一异常处理类
 */

@ControllerAdvice
public class GlobalExceptionHandler  {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse processException(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Exception exception){
        //是否为自定义异常
        if (exception instanceof BusinessException ) {
            LOGGER.info(" >>>>>>>>>>>>>>>>>>>>>>  自定义异常  <<<<<<<<<<<<<<<<<<<<<<");
            ErrorCode errorCode = ((BusinessException) exception).getErrorCode();
            return new RestErrorResponse(errorCode.getCode(),errorCode.getDesc());
        }

        LOGGER.info(" >>>>>>>>>>>>>>>>>>>>>> 未知异常  <<<<<<<<<<<<<<<<<<<<<<");
        return new RestErrorResponse(CommonErrorCode.UNKOWN.getCode(),
                CommonErrorCode.UNKOWN.getDesc());

    }
}
