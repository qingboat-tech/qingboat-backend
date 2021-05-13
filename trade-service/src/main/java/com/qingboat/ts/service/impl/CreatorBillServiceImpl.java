package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.dao.CreatorBillDao;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.service.CreatorBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreatorBillServiceImpl extends ServiceImpl <CreatorBillDao, CreatorBillEntity> implements CreatorBillService {
}

