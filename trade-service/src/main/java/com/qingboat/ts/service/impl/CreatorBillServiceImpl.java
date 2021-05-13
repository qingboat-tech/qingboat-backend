package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.dao.CreatorBillDao;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CreatorBillServiceImpl extends ServiceImpl <CreatorBillDao, CreatorBillEntity> implements CreatorBillService {

    @Override
    public Long getCurrentMonthIncome(Long creatorId) {
        // 获取当月的第一天和最后一天
        Date begining, end;
        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
        }

        // query 数据表
        QueryWrapper<CreatorBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CreatorId",creatorId)
                .ge("billTime",begining)
                .le("billTime",end);
        queryWrapper.select(" sum(amount) as amount");

        CreatorBillEntity creatorBillEntity = this.getOne(queryWrapper);

        return creatorBillEntity.getAmount();
    }

    @Override
    public IPage<CreatorBillEntity> getCreatorBillList(Long creatorId ,Integer pageIndex,Integer pageSize) {
        if (pageSize == null || pageSize<1){
            pageSize =10;
        }
        if (pageIndex == null || pageIndex<1){
            pageIndex =1;
        }
        IPage<CreatorBillEntity> page = new Page<>(pageIndex, pageSize);

        QueryWrapper<CreatorBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId);
        queryWrapper.orderByDesc("bill_time");

        return this.page(page,queryWrapper);
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

}

