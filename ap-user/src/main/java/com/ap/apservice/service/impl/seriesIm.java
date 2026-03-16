package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.seriesMapper;
import com.ap.apcommon.entity.user.subsrcibe;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.seriesService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service("user-series")
public class seriesIm implements seriesService {

    @Autowired
    private seriesMapper seriesMapper;

    @Autowired
    private Client meiliClient;

    @Override
    public R getAllSeries() {
        QueryWrapper<series> queryWrapper = new QueryWrapper<>();
        // 筛选已上架的剧集 (假设 status=1 为上架)
        queryWrapper.eq("status", 1);
        List<series> list = seriesMapper.selectList(queryWrapper);
        return R.ok("查询成功", list);
    }

    @Override
    public R getSeriesByTitle(String title) {
        try {
            SearchResult result = meiliClient.index("series").search(title);
            ArrayList<HashMap<String, Object>> hits = result.getHits();

            List<series> list = hits.stream().map(hit -> {
                Integer id = Integer.valueOf(hit.get("id").toString());
                return seriesMapper.selectById(id);
            }).filter(s -> s != null && s.getStatus() == 1).collect(Collectors.toList());

            return R.ok("搜索成功", list);
        } catch (Exception e) {
            QueryWrapper<series> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("title", title);
            queryWrapper.eq("status", 1);
            List<series> list = seriesMapper.selectList(queryWrapper);
            return R.ok("搜索成功(DB)", list);
        }
    }

    @Override
    public R getSeriesByCategory(Long categoryId) {
        QueryWrapper<series> queryWrapper = new QueryWrapper<>();
        // series 实体中 cid 为 String 类型
        queryWrapper.eq("category_id", String.valueOf(categoryId));
        queryWrapper.eq("status", 1);
        List<series> list = seriesMapper.selectList(queryWrapper);
        return R.ok("查询成功", list);
    }

    @Override
    public R getSeriesById (Long seriesId) {
        QueryWrapper<series> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", seriesId);
        queryWrapper.eq("status", 1);
        series series = seriesMapper.selectOne(queryWrapper);
        return R.ok("查询成功", series);
    }

}
