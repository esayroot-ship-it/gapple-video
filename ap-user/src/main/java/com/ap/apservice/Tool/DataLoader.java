package com.ap.apservice.Tool;

import com.ap.apcommon.dao.announMapper;
import com.ap.apcommon.dao.bannerMapper;
import com.ap.apcommon.entity.announcement;
import com.ap.apcommon.entity.banner;
import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.AnnouncementCacheUtils;
import com.ap.apcommon.tools.BannerCacheUtils;
import com.ap.apcommon.tools.UserCacheUtils;
import com.ap.apservice.dao.userMapper2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private announMapper announMapper;

    @Autowired
    private bannerMapper bannerMapper;

    @Autowired
    private userMapper2 userMapper2;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 加载公告数据
        List<announcement> list = announMapper.selectList(null);
        if (list != null) {
            String key = AnnouncementCacheUtils.ANNOUNCEMENT_LIST_KEY;
            // 启动时先清理旧缓存，防止脏数据
            redisTemplate.delete(key);
            
            for (announcement a : list) {
                // 使用 ZSet 存储，score 为 id，保证有序且不重复
                // id 作为排序依据，存入的对象会自动去重
                redisTemplate.opsForZSet().add(key, a, a.getId());
            }
            // 设置 1 天过期
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }

        // 2. 加载用户数据
        List<User> userList = userMapper2.selectList(null);
        if (userList != null) {
            for (User user : userList) {
                String userKey = UserCacheUtils.getKey(user.getId());
                // 设置1天过期
                redisTemplate.opsForValue().set(userKey, user, 1, TimeUnit.DAYS);
            }
        }

        // 3. 加载 Banner 数据
        List<banner> bannerList = bannerMapper.selectList(null);
        if (bannerList != null) {
            String bannerKey = BannerCacheUtils.BANNER_LIST_KEY;
            // 启动时先清理旧缓存
            redisTemplate.delete(bannerKey);

            for (banner b : bannerList) {
                // 使用 Set 存储
                redisTemplate.opsForSet().add(bannerKey, b);
            }
            // 设置 1 天过期
            redisTemplate.expire(bannerKey, 1, TimeUnit.DAYS);
        }
    }
}
