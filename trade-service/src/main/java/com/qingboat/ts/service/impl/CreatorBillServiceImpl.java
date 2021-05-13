package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.dao.CreatorBillDao;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorBillService;
import com.qingboat.ts.service.CreatorWalletService;
import com.qingboat.ts.utils.CalendarUtil;
import com.qingboat.ts.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CreatorBillServiceImpl extends ServiceImpl <CreatorBillDao, CreatorBillEntity> implements CreatorBillService {

    @Autowired
    private CreatorWalletService creatorWalletService;

    @Override
    @Transactional
    public CreatorBillEntity createBillAndUpdateWallet(CreatorBillEntity creatorBillEntity) {

        if (creatorBillEntity.getCreatorId() == null ||
                creatorBillEntity.getBillTitle() == null ||
                creatorBillEntity.getBillType() == null ||
                creatorBillEntity.getAmount() == null ||
                creatorBillEntity.getOrderNo() == null){
            throw new BaseException(500,"创建账单参数错误");
        }
        creatorBillEntity.setBillTime(new Date());
        boolean rst = this.save(creatorBillEntity);
        if (rst){
            creatorWalletService.updateBalanceByCreatorId(creatorBillEntity.getCreatorId(),creatorBillEntity.getAmount());
        }
        return creatorBillEntity;
    }

    @Override
    public Long getCurrentMonthIncome(Long creatorId) {
        // 获取当月的第一天和最后一天
        Date begining, end;
        {
            Calendar calendar = CalendarUtil.getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            CalendarUtil.setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
        }

        {
            Calendar calendar = CalendarUtil.getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            CalendarUtil.setTimeToEndofDay(calendar);
            end = calendar.getTime();
        }

        // query 数据表
        QueryWrapper<CreatorBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId)
                .between("bill_time",begining,end);
        queryWrapper.select(" sum(amount) as amount");

        CreatorBillEntity creatorBillEntity = this.getOne(queryWrapper);
        if (creatorBillEntity == null){
            return  0l;
        }
        return creatorBillEntity.getAmount();
    }

    @Override
    public IPage<CreatorBillEntity> getCreatorBillList(Long creatorId ,Integer pageIndex,Integer pageSize,Date startTime, Date endTime) {
        if (pageSize == null || pageSize<1){
            pageSize =10;
        }
        if (pageIndex == null || pageIndex<1){
            pageIndex =1;
        }
        IPage<CreatorBillEntity> page = new Page<>(pageIndex, pageSize);

        QueryWrapper<CreatorBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId);
        if (startTime!=null && endTime!=null){
            startTime = DateUtil.getDayBeginTime(startTime);
            endTime = DateUtil.getDayEndTime(endTime);
            queryWrapper.between("bill_time",startTime,endTime);
        }

        queryWrapper.orderByDesc("bill_time");

        return this.page(page,queryWrapper);
    }

}

