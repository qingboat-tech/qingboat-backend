package com.qingboat.base.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingboat.base.api.ApiResponse;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class FeignDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

        String body = Util.toString(response.body().asReader());

        String clazz = String.class.getTypeName();
        if (type!=null && clazz.equals(type.getTypeName())){
            return body;
        }

        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory()
                .constructParametricType(ApiResponse.class, mapper.getTypeFactory().constructType(type));
        ApiResponse<?> apiResponse = mapper.readValue(body, javaType);
        return apiResponse.getData();
    }

}
