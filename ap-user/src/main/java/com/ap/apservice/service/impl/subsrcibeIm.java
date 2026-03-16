package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.seriesMapper;
import com.ap.apcommon.entity.user.subsrcibe;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.R;
import com.ap.apservice.dao.subsrcibeMapper;
import com.ap.apservice.service.subsrcibeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("user-subscribe")
public class subsrcibeIm implements subsrcibeService {

    @Autowired
    private subsrcibeMapper subsrcibeMapper;

    @Autowired
    private seriesMapper seriesMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R addSubscribe(Long userId, Long seriesId) {
        if (userId == null || seriesId == null) {
            return R.fail("参数错误");
        }

        QueryWrapper<subsrcibe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("series_id", seriesId);

        if (subsrcibeMapper.selectCount(queryWrapper) > 0) {
            return R.fail("已订阅");
        }

        subsrcibe sub = new subsrcibe();
        sub.setUid(userId.intValue());
        sub.setSid(seriesId.intValue());
        sub.setStatus((byte) 1);
        sub.setCreatedtime(LocalDateTime.now());

        int rows = subsrcibeMapper.insert(sub);
        return rows > 0 ? R.ok("订阅成功") : R.fail("订阅失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteSubscribe(Long userId, Long seriesId) {
        QueryWrapper<subsrcibe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("series_id", seriesId);

        int rows = subsrcibeMapper.delete(queryWrapper);
        return rows > 0 ? R.ok("取消订阅成功") : R.fail("未找到订阅记录");
    }

    @Override
    public R getSubscribes(Long userId) {
        QueryWrapper<subsrcibe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        List<subsrcibe> subList = subsrcibeMapper.selectList(queryWrapper);
        if (subList == null || subList.isEmpty()) {
            return R.ok("暂无订阅", Collections.emptyList());
        }

        List<Long> seriesIds = subList.stream()
                .map(s -> Long.valueOf(s.getSid()))
                .collect(Collectors.toList());

        List<series> seriesList = seriesMapper.selectBatchIds(seriesIds);
        return R.ok("查询成功", seriesList);
    }

    @Override
    public R getIfSubscribed(Long userId, Long seriesId) {
        QueryWrapper<subsrcibe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("series_id", seriesId);

        Long count = subsrcibeMapper.selectCount(queryWrapper);
        return count > 0 ? R.ok("已订阅") : R.fail("未订阅");
    }
}
