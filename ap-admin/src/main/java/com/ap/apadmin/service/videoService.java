package com.ap.apadmin.service;

import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.tools.R;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

public interface videoService {

    /**
     * 获取所有视频列表
     */
    R getAllVideos();

    /**
     * 根据ID获取视频详情
     */
    R getVideoById(Integer id);

    /**
     * 创建视频
     * 包含文件上传逻辑：视频文件 -> video文件夹, 封面 -> video文件夹
     */
    R createVideo(Video video);

    /**
     * 更新视频信息
     */
    R updateVideo(Video video);

    /**
     * 删除视频
     * 同时删除本地关联的物理文件
     */
    R deleteVideo(Integer id);

}
