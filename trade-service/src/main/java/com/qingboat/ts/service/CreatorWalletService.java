package com.qingboat.ts.service;


import com.qingboat.ts.entity.CreatorWalletEntity;

import java.util.Map;

public interface CreatorWalletService {

    /**
     * 查询创作者总余额和收益
     * @param creatorId
     * @return
     */
    CreatorWalletEntity getCreatorWalletByCreatorId(Long creatorId);

}
