package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.videoMapper;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.rating;
import com.ap.apcommon.tools.R;
import com.ap.apservice.dao.ratingMapper;
import com.ap.apservice.service.ratingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ratingIm implements ratingService {

    @Autowired
    private ratingMapper ratingMapper;

    @Autowired
    private videoMapper videoMapper;

    @Override
    @Transactional
    public R addRating(Long userId, Long videoId, Integer score) {
        LambdaQueryWrapper<rating> qw = new LambdaQueryWrapper<>();
        qw.eq(rating::getUid, userId);
        qw.eq(rating::getVid, videoId);

        rating exist = ratingMapper.selectOne(qw);

        if (exist != null) {
            // Update existing rating
            exist.setScore(score);
            exist.setUpdatedtime(LocalDateTime.now());
            ratingMapper.updateById(exist);
        } else {
            // Add new rating
            rating newRating = new rating();
            newRating.setUid(userId);
            newRating.setVid(videoId);
            newRating.setScore(score);
            newRating.setCreatedtime(LocalDateTime.now());
            newRating.setUpdatedtime(LocalDateTime.now());
            ratingMapper.insert(newRating);
        }
        
        // Recalculate average score
        updateVideoAverage(videoId);
        
        return R.ok("成功");
    }

    @Override
    @Transactional
    public R deleteRating(Long userId, Long videoId) {
        LambdaQueryWrapper<rating> qw = new LambdaQueryWrapper<>();
        qw.eq(rating::getUid, userId);
        qw.eq(rating::getVid, videoId);

        int count = ratingMapper.delete(qw);
        if (count > 0) {
            // Recalculate average score
            updateVideoAverage(videoId);
            return R.ok("成功");
        }
        return R.fail("未找到相关评分记录");
    }

    @Override
    public R getUserRating(Long userId, Long videoId) {
        LambdaQueryWrapper<rating> qw = new LambdaQueryWrapper<>();
        qw.eq(rating::getUid, userId);
        qw.eq(rating::getVid, videoId);
        rating r = ratingMapper.selectOne(qw);
        if (r == null) {
            return R.fail("未找到该用户的评分数据");
        }
        return R.ok("成功", r);
    }

    @Override
    public R getRatingByVideoId(Long videoId) {
        LambdaQueryWrapper<rating> qw = new LambdaQueryWrapper<>();
        qw.eq(rating::getVid, videoId);
        List<rating> list = ratingMapper.selectList(qw);
        if (list == null || list.isEmpty()) {
            return R.fail("该视频暂无评分数据");
        }
        return R.ok("成功", list);
    }

    private void updateVideoAverage(Long videoId) {
        LambdaQueryWrapper<rating> qw = new LambdaQueryWrapper<>();
        qw.eq(rating::getVid, videoId);
        List<rating> ratings = ratingMapper.selectList(qw);

        if (ratings == null || ratings.isEmpty()) {
            Video video = videoMapper.selectById(videoId);
            if (video != null) {
                video.setAvgRating(BigDecimal.ZERO);
                video.setRatingCount(0);
                videoMapper.updateById(video);
            }
        } else {
            double sum = ratings.stream().mapToInt(rating::getScore).sum();
            BigDecimal avg = BigDecimal.valueOf(sum)
                    .divide(BigDecimal.valueOf(ratings.size()), 1, RoundingMode.HALF_UP);
            
            Video video = videoMapper.selectById(videoId);
            if (video != null) {
                video.setAvgRating(avg);
                video.setRatingCount(ratings.size());
                videoMapper.updateById(video);
            }
        }
    }
}