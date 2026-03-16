package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.commentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "用户-评论管理", description = "用户端评论相关接口")
@RestController
@RequestMapping("/u/comment")
public class commentCon {

    @Autowired
    private commentService commentService;

    /**
     * 发表评论
     * @param userId 用户ID (暂由前端传入)
     * @param videoId 视频ID
     * @param content 评论内容
     */
    @Operation(summary = "发表评论")
    @PostMapping("/add")
    public R<Void> addComment(@RequestParam Integer userId,
                        @RequestParam Integer videoId,
                        @RequestParam String content) {
        return commentService.addComment(userId, videoId, content);
    }

    /**
     * 获取视频的所有评论
     * @param videoId 视频ID
     */
    @Operation(summary = "获取视频评论")
    @GetMapping("/list")
    public R<List<com.ap.apcommon.entity.user.comment>> getComments(@RequestParam Integer videoId) {
        return commentService.getCommentsByVideoId(videoId);
    }
}
