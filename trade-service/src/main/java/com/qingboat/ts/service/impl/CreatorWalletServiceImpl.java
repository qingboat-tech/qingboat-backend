package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.dao.CreatorWalletDao;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CreatorWalletServiceImpl extends ServiceImpl <CreatorWalletDao, CreatorWalletEntity> implements CreatorWalletService {


    @Override
    public CreatorWalletEntity getCreatorWalletByCreatorId(Long creatorId){
        CreatorWalletEntity walletEntity = new CreatorWalletEntity();
        walletEntity.setCreatorId(creatorId);
        QueryWrapper<CreatorWalletEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(walletEntity);
        walletEntity = this.getOne(queryWrapper);
        return walletEntity;
    }
}

