package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.dao.BillDao;
import com.qingboat.ts.entity.BillEntity;
import com.qingboat.ts.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillServiceImpl extends ServiceImpl <BillDao, BillEntity> implements BillService {
}

