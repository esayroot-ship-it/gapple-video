package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.episodeMapper;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.episodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("user-episodeIm")
public class episodeIm implements episodeService {

    @Autowired
    private episodeMapper episodeMapper;

    @Override
    public R getEpisodesBySeriesId(Long seriesId) {
        LambdaQueryWrapper<episode> qw = new LambdaQueryWrapper<>();
        // Entity field 'sid' maps to 'series_id', converting Long to Integer to match entity type
        qw.eq(episode::getSid, seriesId.intValue());
        if (episodeMapper.selectList(qw).size() > 0) return R.ok("成功", episodeMapper.selectList(qw));
        else return R.fail("失败");
    }

    @Override
    public R getSeriesByVideoId(Long videoId) {
        LambdaQueryWrapper<episode> qw = new LambdaQueryWrapper<>();
        qw.eq(episode::getVid, videoId);
        if (episodeMapper.selectList(qw).size() > 0) return R.ok("成功", episodeMapper.selectList(qw));
        else return R.fail("失败");
    }
}
