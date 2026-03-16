package com.ap.apadmin.service.impl;

import com.ap.apadmin.service.bannerService;
import com.ap.apcommon.entity.banner;
import com.ap.apcommon.tools.BannerCacheUtils;
import com.ap.apcommon.tools.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ap.apcommon.dao.bannerMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class bannerServiceIm implements bannerService {

        @Autowired
        private bannerMapper bannerMapper;

        @Autowired
        private RedisTemplate<String, Object> redisTemplate;

        @Override
        @Transactional
        public R createBanner(banner banner) {
            int rows = bannerMapper.insert(banner);
            if (rows > 0) {
                // 新建 Banner 写入 Redis
                String key = BannerCacheUtils.BANNER_LIST_KEY;
                redisTemplate.opsForSet().add(key, banner);
                // 刷新过期时间（可选，保证活跃）
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
                return R.ok("Banner创建成功");
            }
            return R.fail("创建失败");
        }

        @Override
        public R deleteBanner(Integer id) {
            int rows = bannerMapper.deleteById(id);
            if (rows > 0) {
                // 删除操作 -> 删除整个缓存 Key
                redisTemplate.delete(BannerCacheUtils.BANNER_LIST_KEY);
                return R.ok("Banner删除成功");
            }
            return R.fail("删除失败");
        }

        @Override
        @Transactional
        public R updateBanner(banner banner) {
            int rows = bannerMapper.updateById(banner);
            if (rows > 0) {
                // 更新操作 -> 删除整个缓存 Key
                redisTemplate.delete(BannerCacheUtils.BANNER_LIST_KEY);
                return R.ok("Banner更新成功");
            }
            return R.fail("更新失败");
        }

        @Override
        public R getBanner(Integer id) {
            com.ap.apcommon.entity.banner banner = bannerMapper.selectById(id);
            return banner != null ? R.ok("查询成功", banner) : R.fail("未找到该Banner");
        }

        @Override
        public R getallBanner() {
            List<banner> list = bannerMapper.selectList(null);
            return list != null ? R.ok("查询成功", list) : R.fail("未找到Banner");
        }

        @Override
        public R updateStatus(Integer id, Integer status) {
            com.ap.apcommon.entity.banner banner = bannerMapper.selectById(id);
            if (banner == null) {
                return R.fail("未找到该Banner");
            }
            banner.setStatus(status);
            int rows = bannerMapper.updateById(banner);
            if (rows > 0) {
                // 状态更新 -> 删除整个缓存 Key
                redisTemplate.delete(BannerCacheUtils.BANNER_LIST_KEY);
                return R.ok("Banner状态更新成功");
            }
            return R.fail("状态更新失败");
        }
}

