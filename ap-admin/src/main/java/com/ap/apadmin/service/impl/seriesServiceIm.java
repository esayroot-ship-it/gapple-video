package com.ap.apadmin.service.impl;

import com.ap.apcommon.dao.episodeMapper;
import com.ap.apcommon.dao.seriesMapper;
import com.ap.apadmin.service.seriesService;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class seriesServiceIm implements seriesService {

    @Autowired
    private seriesMapper seriesMapper;

    @Autowired
    private episodeMapper episodeMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "feedback.exchange";
    private static final String SERIES_ROUTING_KEY = "series.sync";

    @Value("${file-storage.series-path}")
    private String uploadDir;

    // 对应前端访问的URL前缀
    private static final String URL_PREFIX = "/simg/";

    @Override
    public R getAllSeries() {
        List<series> list = seriesMapper.selectList(null);
        return list != null ? R.ok("查询成功", list) : R.fail("暂无剧集");
    }

    @Override
    public R getSeriesById(Long id) {
        series s = seriesMapper.selectById(id);
        return s != null ? R.ok("查询成功", s) : R.fail("未找到该剧集");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createSeries(series series) {
        // 1. 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 2. 处理封面文件
            MultipartFile file = series.getFile();
            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                File dest = new File(dir, fileName);
                file.transferTo(dest);
                // 设置数据库路径
                series.setCoverUrl(URL_PREFIX + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("文件上传发生错误: " + e.getMessage());
        }

        if (series.getCreatedtime() == null) {
            series.setCreatedtime(LocalDateTime.now());
        }
        // 初始状态下分集数通常为0 (如果未传值)
        if (series.getEcount() == null) {
            series.setEcount((byte) 0);
        }

        int rows = seriesMapper.insert(series);
        if (rows > 0) {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, SERIES_ROUTING_KEY, series);
        }
        return rows > 0 ? R.ok("剧集创建成功") : R.fail("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateSeries(series series) {
        series.setUpdatedtime(LocalDateTime.now());
        int rows = seriesMapper.updateById(series);
        if (rows > 0) {
            series updatedSeries = seriesMapper.selectById(series.getId());
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, SERIES_ROUTING_KEY, updatedSeries);
        }
        return rows > 0 ? R.ok("剧集更新成功") : R.fail("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteSeries(Long id) {
        // 检查是否有关联的分集，如果有，建议先提示删除分集，或者级联删除
        // 这里采用安全策略：如果有分集则禁止删除
        QueryWrapper<episode> wrapper = new QueryWrapper<>();
        // 注意：episode 实体中 sid 是 Integer，series 实体 id 是 Long，这里需要强转
        wrapper.eq("series_id", id.intValue());
        Long count = episodeMapper.selectCount(wrapper);

        if (count > 0) {
            return R.fail("该剧集下包含 " + count + " 个分集，请先删除分集");
        }

        // 删除物理文件 (可选，参考 videoService 实现，若需清理垃圾文件可解开如下逻辑)

     series s = seriesMapper.selectById(id);
    if (s != null && s.getCoverUrl() != null) {
         String fileName = s.getCoverUrl().replace(URL_PREFIX, "");
         File file = new File(uploadDir + fileName);
         if (file.exists()) file.delete();
    }


        int rows = seriesMapper.deleteById(id);
        if (rows > 0 && s != null) {
            s.setStatus((byte) 0); // 标记删除同步到 Meilisearch
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, SERIES_ROUTING_KEY, s);
        }
        return rows > 0 ? R.ok("剧集删除成功") : R.fail("删除失败");
    }
}
