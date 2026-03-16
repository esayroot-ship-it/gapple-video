package com.ap.apadmin.service.impl;

import com.ap.apcommon.dao.TopicVideoMapper;
import com.ap.apcommon.dao.VideoTagMapper;
import com.ap.apcommon.dao.episodeMapper;
import com.ap.apcommon.dao.videoMapper;
import com.ap.apadmin.service.videoService;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.episode;
import com.ap.apcommon.entity.video.topicvideo;
import com.ap.apcommon.entity.video.videotag;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service("videoService")
public class videoServiceIm implements videoService {

    @Autowired
    private videoMapper videoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "feedback.exchange"; // 使用已有的交换机
    private static final String VIDEO_ROUTING_KEY = "video.sync";

    @Autowired
    private TopicVideoMapper topicVideoMapper;

    @Autowired
    private VideoTagMapper videoTagMapper;

    @Autowired
    private episodeMapper episodeMapper;

    @Value("${file-storage.video-path}")
    private String uploadDir;

    // 对应前端访问的URL前缀，需要在WebConfig配置资源映射
    private static final String URL_PREFIX = "/video/";

    @Override
    public R getAllVideos() {
        List<Video> list = videoMapper.selectList(null);
        return R.ok("查询成功", list);
    }

    @Override
    public R getVideoById(Integer id) {
        Video video = videoMapper.selectById(id);
        return video != null ? R.ok("查询成功", video) : R.fail("未找到该视频");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createVideo(Video video) {
        // 1. 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 2. 处理视频文件
            MultipartFile vFile = video.getVideofile();
            if (vFile != null && !vFile.isEmpty()) {
                // 方案A：保持原文件名 (存在覆盖风险，但符合你的要求)
                String vFileName = vFile.getOriginalFilename();
                // 方案B：如果要避免覆盖，建议用 UUID: String vFileName = UUID.randomUUID() + "_" + vFile.getOriginalFilename();

                File destV = new File(dir, vFileName);
                vFile.transferTo(destV);
                // 设置数据库路径
                video.setPlayUrl(URL_PREFIX + vFileName);
            }

            // 3. 处理封面文件
            MultipartFile cFile = video.getCoverfile();
            if (cFile != null && !cFile.isEmpty()) {
                String cFileName = cFile.getOriginalFilename();
                File destC = new File(dir, cFileName);
                cFile.transferTo(destC);
                // 设置数据库路径
                video.setCoverUrl(URL_PREFIX + cFileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("文件上传发生错误: " + e.getMessage());
        }

        // 4. 设置默认值
        if (video.getCreatedAt() == null) {
            video.setCreatedAt(LocalDateTime.now());
        }
        // status 默认为 1 (上架)
        if (video.getStatus() == null) {
            video.setStatus(1);
        }

        // 5. 保存到数据库
        int rows = videoMapper.insert(video);
        if (rows > 0) {
            // 发送增量同步消息
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, VIDEO_ROUTING_KEY, video);
        }
        return rows > 0 ? R.ok("视频创建成功") : R.fail("保存失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateVideo(Video video) {
        // 更新逻辑通常只更新文本信息，若需更新文件可参考 createVideo 逻辑
        video.setUpdatedAt(LocalDateTime.now());
        int rows = videoMapper.updateById(video);
        if (rows > 0) {
            // 获取最新数据并同步
            Video updatedVideo = videoMapper.selectById(video.getId());
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, VIDEO_ROUTING_KEY, updatedVideo);
        }
        return rows > 0 ? R.ok("更新成功") : R.fail("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteVideo(Integer id) {
        // 1. 获取视频信息以拿到文件路径
        Video video = videoMapper.selectById(id);
        if (video == null) {
            return R.fail("视频不存在");
        }

        // 2. 删除物理文件
        deleteFile(video.getPlayUrl());
        deleteFile(video.getCoverUrl());


        // 3. 删除数据库记录
        int rows = videoMapper.deleteById(id);
        if (rows > 0) {
            // 通知 MQ 删除索引 (设置状态为 0 或发送特殊消息，消费者已处理 status != 1 的情况)
            video.setStatus(0); 
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, VIDEO_ROUTING_KEY, video);
        }
        return rows > 0 ? R.ok("删除成功") : R.fail("删除失败");
    }

    /**
     * 辅助方法：删除文件
     * @param urlDbPath 数据库中存储的路径 (如 /video/abc.mp4)
     */
    private void deleteFile(String urlDbPath) {
        if (urlDbPath == null || urlDbPath.isEmpty()) {
            return;
        }
        // 将URL路径转换为本地绝对路径
        // 去掉前缀 /video/ -> abc.mp4
        String fileName = urlDbPath.replace(URL_PREFIX, "");
        // 拼接 -> D:\code\apvideos\video\abc.mp4
        // 注意：这里需要处理路径分隔符，确保健壮性

        Logger.getLogger("videoServiceIm").info("1. 准备删除 ID " );
        Logger.getLogger("videoServiceIm").info("2. 数据库中记录的路径(Url/Path)是: " + urlDbPath);

        File file = new File(uploadDir + fileName);

        if (file.exists() && file.isFile()) {
            file.delete();
            Logger.getLogger("videoServiceIm").info("删除文件成功: " + file.getAbsolutePath());
        }
    }

}
