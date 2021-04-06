package com.qingboat.base.advise;

import com.qingboat.base.api.ApiResponse;
import com.qingboat.base.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionResponseBodyAdvice<T>   {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse sendErrorResponse_UserDefined(BaseException exception){
        return new ApiResponse(ex.getCode(), ex.getMessage(), ex.fillInStackTrace());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse sendErrorResponse_System(Exception exception){
        if (exception instanceof BaseException){
            BaseException ex = ((BaseException)exception);
            return new ApiResponse(ex.getCode(), ex.getMessage(), ex.fillInStackTrace());
        }
        return new ApiResponse(500, ApiResponse.ERROR, exception.toString());
    }


}
