package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.commentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "用户-评论点赞管理", description = "用户端评论点赞相关接口")
@RestController
@RequestMapping("/u/commentlike")
public class commentLikeCon {

    @Autowired
    private commentLikeService commentLikeService;

    /**
     * 给评论点赞
     * @param userId 用户ID (暂由前端传入)
     * @param commentId 评论ID
     */
    @Operation(summary = "给评论点赞")
    @PostMapping("/add")
    public R<Void> likeComment(@RequestParam Integer userId,
                               @RequestParam Integer commentId) {
        return commentLikeService.likeComment(userId, commentId);
    }

    /**
     * 获取评论点赞数
     * @param commentId 评论ID
     */
    @Operation(summary = "获取评论点赞数")
    @GetMapping("/count")
    public R<Integer> getLikeCount(@RequestParam Integer commentId) {
        return commentLikeService.getLikeCount(commentId);
    }
}
