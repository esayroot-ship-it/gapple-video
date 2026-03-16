package com.ap.apadmin.service;

import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.tools.R;

public interface episodeService {
    /**
     * 获取指定剧集下的所有分集
     * 通常按集数 (episode_no) 排序
     * @param seriesId 剧集ID
     * @return 分集列表
     */
    R getEpisodesBySeriesId(Long seriesId);

    /**
     * 获取单个分集详情
     * @param id 分集ID
     * @return 分集详情
     */
    R getEpisodeById(Integer id);

    /**
     * 创建/添加分集
     * 需要在入参中包含 series_id (sid) 和 video_id (vid)
     * @param episode 分集实体
     * @return 操作结果
     */
    R createEpisode(episode episode);

    /**
     * 删除分集
     * 解除剧集和视频的关联，通常不删除视频文件本身
     * @param id 分集ID
     * @return 操作结果
     */
    R deleteEpisode(Integer id);
}

