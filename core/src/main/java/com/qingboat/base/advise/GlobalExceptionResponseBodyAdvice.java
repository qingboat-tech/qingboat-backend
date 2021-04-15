package com.qingboat.base.advise;

import com.qingboat.base.api.ApiResponse;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class GlobalExceptionResponseBodyAdvice<T>   {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendErrorResponse_UserDefined(Exception exception){
        BaseException ex = ((BaseException)exception);
        log.error("GlobalExceptionResponseBodyAdvice",ex);
        return new ApiResponse(ex.getCode(), ex.getMessage(),null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendErrorResponse_System(Exception exception){
        log.error("GlobalExceptionResponseBodyAdvice",exception);
        return new ApiResponse(500, ApiResponse.ERROR, exception.toString());
    }


}
