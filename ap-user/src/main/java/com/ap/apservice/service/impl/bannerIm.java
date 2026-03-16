package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.bannerMapper;
import com.ap.apcommon.entity.banner;
import com.ap.apcommon.tools.BannerCacheUtils;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.bannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("user-bannerService")
public class bannerIm implements bannerService {

    @Autowired
    private bannerMapper bannerMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public R getallBanner() {
        String key = BannerCacheUtils.BANNER_LIST_KEY;

        // 1. 查缓存
        Set<Object> cachedList = redisTemplate.opsForSet().members(key);
        if (cachedList != null && !cachedList.isEmpty()) {
            return R.ok("成功", cachedList);
        }

        // 2. 查数据库
        List<banner> list = bannerMapper.selectList(null);

        // 3. 写缓存
        if (list != null && !list.isEmpty()) {
            for (banner b : list) {
                redisTemplate.opsForSet().add(key, b);
            }
            // 设置 1 天过期
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }

        return R.ok("成功", list);
    }
}
