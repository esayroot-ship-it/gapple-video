package com.ap.apadmin.service.impl;

import com.ap.apcommon.dao.episodeMapper;
import com.ap.apcommon.dao.seriesMapper;
import com.ap.apcommon.dao.videoMapper;
import com.ap.apadmin.service.episodeService;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class episodeServiceIm implements episodeService {

    @Autowired
    private episodeMapper episodeMapper;

    @Autowired
    private seriesMapper seriesMapper;

    @Autowired
    private videoMapper videoMapper;
     // 用于校验视频是否存在

    @Override
    public R getEpisodesBySeriesId(Long seriesId) {
        QueryWrapper<episode> wrapper = new QueryWrapper<>();
        wrapper.eq("series_id", seriesId.intValue()); // 类型转换 Long -> Integer
        wrapper.orderByAsc("episode_no"); // 按集数正序排列

        List<episode> list = episodeMapper.selectList(wrapper);
        return list != null ? R.ok("查询成功", list) : R.fail("该剧集暂无分集");
    }

    @Override
    public R getEpisodeById(Integer id) {
        episode e = episodeMapper.selectById(id);
        return e != null ? R.ok("查询成功", e) : R.fail("未找到分集信息");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createEpisode(episode episode) {
        // 1. 校验必填项
        if (episode.getSid() == null || episode.getVid() == null) {
            return R.fail("剧集ID(sid)和视频ID(vid)不能为空");
        }

        // 2. 校验关联数据是否存在
        series s = seriesMapper.selectById(episode.getSid().longValue());
        if (s == null) return R.fail("关联的剧集不存在");

        Video v = videoMapper.selectById(episode.getVid().longValue());
        if (v == null) return R.fail("关联的视频资源不存在");

        // 3. 校验集数是否冲突 (可选逻辑)
        QueryWrapper<episode> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("series_id", episode.getSid())
                .eq("episode_no", episode.getNo());
        if (episodeMapper.selectCount(checkWrapper) > 0) {
            return R.fail("该剧集已存在第 " + episode.getNo() + " 集");
        }

        // 4. 保存分集
        if (episode.getCreated() == null) {
            episode.setCreated(LocalDateTime.now());
        }
        int rows = episodeMapper.insert(episode);

        if (rows > 0) {
            // 5. 可选：更新剧集的总集数或更新时间
             s.setEcount((byte) (s.getEcount() + 1));
             seriesMapper.updateById(s);
            return R.ok("分集添加成功");
        }
        return R.fail("添加失败");
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public R updateEpisode(episode episode) {
//        // 仅允许修改 标题、集数、状态等，不建议修改所属剧集和视频ID，除非特殊业务
//        int rows = episodeMapper.updateById(episode);
//        return rows > 0 ? R.ok("分集更新成功") : R.fail("更新失败");
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteEpisode(Integer id) {
        int rows = episodeMapper.deleteById(id);
        return rows > 0 ? R.ok("分集删除成功") : R.fail("删除失败");
    }
}
