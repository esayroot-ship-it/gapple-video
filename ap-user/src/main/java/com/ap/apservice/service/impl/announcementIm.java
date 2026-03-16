package com.ap.apservice.service.impl;


import com.ap.apcommon.dao.announMapper;
import com.ap.apcommon.entity.announcement;
import com.ap.apcommon.tools.AnnouncementCacheUtils;
import com.ap.apcommon.tools.R;

import com.ap.apservice.service.announcementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("user-announcementIm")
public class announcementIm  implements announcementService{

    @Autowired
    private announMapper announMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public R getallAnnouncement() {
        String key = AnnouncementCacheUtils.ANNOUNCEMENT_LIST_KEY;

        // 1. 查缓存
        Set<Object> cachedList = redisTemplate.opsForZSet().range(key, 0, -1);
        if (cachedList != null && !cachedList.isEmpty()) {
            return R.ok("成功", cachedList);
        }

        // 2. 查数据库
        List<announcement> list = announMapper.selectList(null);

        // 3. 写缓存
        if (list != null && !list.isEmpty()) {
            for (announcement a : list) {
                redisTemplate.opsForZSet().add(key, a, a.getId());
            }
            // 设置 1 天过期
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }

        return R.ok("成功", list);
    }
}
