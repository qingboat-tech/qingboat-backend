package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.base.exception.BaseException;
import com.qingboat.ts.dao.CreatorWalletDao;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorWalletService;
import com.qingboat.ts.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CreatorWalletServiceImpl extends ServiceImpl <CreatorWalletDao, CreatorWalletEntity> implements CreatorWalletService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public CreatorWalletEntity getCreatorWalletByCreatorId(Long creatorId){
        QueryWrapper<CreatorWalletEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId);
        CreatorWalletEntity walletEntity = this.getOne(queryWrapper);
        return walletEntity;
    }

    @Override
    public CreatorWalletEntity updateBalanceByCreatorId(Long creatorId, Long amount) {
        //添加redis锁
        String lockKey = "creatorWallet"+creatorId;
        try {
            if (redisUtil.lock(lockKey)) {
                return updateBalance(creatorId,amount);
            }else {
                // 设置失败次数计数器, 当到达5次时, 返回失败
                int failCount = 1;
                while(failCount <= 5){
                    // 等待100ms重试
                    try {
                        Thread.sleep(100l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (redisUtil.lock(lockKey)){
                        return updateBalance(creatorId,amount);
                    }else{
                        failCount ++;
                    }
                }
                throw new BaseException(500,"现在使用的人太多了, 请稍等再试");
            }

        }finally {
            redisUtil.unLock(lockKey);
        }
    }

    private CreatorWalletEntity updateBalance(Long creatorId, Long amount) {
        QueryWrapper<CreatorWalletEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId);
        CreatorWalletEntity entity =this.getOne(queryWrapper);
        if (entity == null){
            entity = new CreatorWalletEntity();
            if (amount>0){
                entity.setIncome(amount);
            }
            entity.setBalance(amount);
            entity.setCreatorId(creatorId);
            this.save(entity);
        }else {
            entity.setBalance(entity.getBalance()+amount);
            if (amount>0){
                entity.setIncome(entity.getIncome()+amount);
            }
            boolean rst = this.updateById(entity);
            if (!rst){
                throw new BaseException(500,"更新创作者钱包失败");
            }
        }
        return entity;
    }
}

