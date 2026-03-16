package com.ap.apadmin.service.impl;

import com.ap.apcommon.dao.announMapper;
import com.ap.apadmin.service.announcementService;
import com.ap.apcommon.entity.announcement;
import com.ap.apcommon.tools.AnnouncementCacheUtils;
import com.ap.apcommon.tools.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class announcementIm implements announcementService {

    @Autowired
    private announMapper announMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public R createAnnouncement(announcement announ) {
        int rows = announMapper.insert(announ);
        if (rows > 0) {
            // 新建公告写入 Redis
            String key = AnnouncementCacheUtils.ANNOUNCEMENT_LIST_KEY;
            redisTemplate.opsForZSet().add(key, announ, announ.getId());
            // 刷新过期时间（可选，保证活跃）
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
            return R.ok("公告创建成功");
        }
        return R.fail("创建失败");
    }

    @Override
    public R deleteAnnouncement(Long id) {
        int rows = announMapper.deleteById(id);
        if (rows > 0) {
            // 删除操作 -> 删除整个缓存 Key
            redisTemplate.delete(AnnouncementCacheUtils.ANNOUNCEMENT_LIST_KEY);
            return R.ok("公告删除成功");
        }
        return R.fail("删除失败");
    }

    @Override
    @Transactional
    public R updateAnnouncement(announcement announ) {
        //利用框架自动更新
        int rows = announMapper.updateById(announ);
        if (rows > 0) {
            // 更新操作 -> 删除整个缓存 Key
            redisTemplate.delete(AnnouncementCacheUtils.ANNOUNCEMENT_LIST_KEY);
            return R.ok("公告更新成功");
        }
        return R.fail("更新失败");
    }

    @Override
    public R getAnnouncement(Long id) {
        com.ap.apcommon.entity.announcement announcement = announMapper.selectById(id);
        return announcement != null ? R.ok("查询成功", announcement) : R.fail("未找到该公告");
    }

    @Override
    @Transactional
    public R getallAnnouncement() {
        List<announcement> list = announMapper.selectList(null);
        return list != null ? R.ok("查询成功", list) : R.fail("未找到公告");
    }

    @Override
    public R updateStatus(Long id, Integer status) {
        com.ap.apcommon.entity.announcement announcement = announMapper.selectById(id);
        if (announcement == null) {
            return R.fail("未找到该公告");
        }
        announcement.setStatus(status);
        int rows = announMapper.updateById(announcement);
        if (rows > 0) {
            // 状态更新 -> 删除整个缓存 Key
            redisTemplate.delete(AnnouncementCacheUtils.ANNOUNCEMENT_LIST_KEY);
            return R.ok("公告状态更新成功");
        }
        return R.fail("状态更新失败");
    }
}
