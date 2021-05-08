package com.qingboat.as.utils.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.qingboat.as.entity.BenefitEntity;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@MappedTypes({BenefitEntity.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class BenefitJsonHandler extends AbstractJsonTypeHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class);
    private final Class<?> type;

    public BenefitJsonHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("FastjsonTypeHandler(" + type + ")");
        }

        Assert.notNull(type, "Type argument cannot be null", new Object[0]);
        this.type = type;

    }

    protected Object parse(String json) {
        return JSON.parseObject(json, new TypeReference<List<BenefitEntity>>(){});
    }

    protected String toJson(Object obj) {
        return JSON.toJSONString(obj, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty});
    }
}


