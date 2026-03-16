package com.ap.apadmin.service;

import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.R;

public interface seriesService {
    /**
     * 获取所有剧集列表
     * @return 包含剧集列表的 R 对象
     */
    R getAllSeries();

    /**
     * 根据ID获取剧集详情
     * @param id 剧集ID
     * @return 包含剧集详情的 R 对象
     */
    R getSeriesById(Long id);

    /**
     * 创建新剧集
     * @param series 剧集实体
     * @return 操作结果
     */
    R createSeries(series series);

    /**
     * 删除剧集
     * 注意：通常删除剧集前需要检查是否包含分集，或者级联删除分集
     * @param id 剧集ID
     * @return 操作结果
     */
    R deleteSeries(Long id);

    /**
     * 更新剧集信息
     * @param series 剧集实体
     * @return 操作结果
     */

    R updateSeries(series series);
}
