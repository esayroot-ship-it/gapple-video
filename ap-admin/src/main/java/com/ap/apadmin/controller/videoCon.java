package com.ap.apadmin.controller;

import com.ap.apadmin.service.videoService;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Tag(name = "视频管理", description = "视频管理相关接口")
@RestController
@RequestMapping("/video")
public class videoCon {

    @Autowired
    private videoService videoService;

    @GetMapping("/all")
    @Operation(summary = "获取所有视频")
    public R<List<Video>> getAll() {
        return videoService.getAllVideos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取视频")
    public R<Video> getById(@PathVariable Integer id) {
        return videoService.getVideoById(id);
    }

    /**
     * 新增视频
     * 注意：前端需使用 multipart/form-data 格式提交
     * 参数不需要 @RequestBody，Spring 会自动绑定表单字段到 Video 对象
     */
    @PostMapping(path = "/add")
    @Operation(summary = "新增视频(含文件上传)")
    public R<Void> add(@RequestParam String title, @RequestParam String description,
                       @RequestParam Long categoryId, @RequestParam Integer status,
                       @RequestParam MultipartFile videofile, @RequestParam MultipartFile coverfile) {
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setCategoryId(categoryId);
        video.setStatus(status);
        video.setVideofile(videofile);
        video.setCoverfile(coverfile);

        return videoService.createVideo(video);
    }

    @PutMapping("/update")
    @Operation(summary = "更新视频信息")
    public R<Void> update(@RequestBody Video video) {
        return videoService.updateVideo(video);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除视频")
    public R<Void> delete(@PathVariable Integer id) {
        return videoService.deleteVideo(id);
    }

}
