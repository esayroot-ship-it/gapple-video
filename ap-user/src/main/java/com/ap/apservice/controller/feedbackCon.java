package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.feedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户-反馈管理", description = "用户端反馈相关接口")
@RestController("user-feedback")
@RequestMapping("/u/feedback")
public class feedbackCon {

    @Autowired
    private feedbackService feedbackService;

    @Operation(summary = "获取用户反馈列表")
    @GetMapping("/list")
    public R<List<com.ap.apcommon.entity.feedback>> getFeedbacksByUserId(@RequestParam Long userId) {
        return feedbackService.getFeedbacksByUserId(userId);
    }

    @Operation(summary = "提交反馈")
    @PostMapping("/add")
    public R<Void> addFeedback(@RequestParam("userId") Long userId,
                         @RequestParam("content") String content,
                               @RequestParam("type") String type) {
        return feedbackService.addFeedback(userId, content, type);
    }

    @Operation(summary = "删除反馈")
    @DeleteMapping("/delete")
    public R<Void> deleteFeedback(@RequestParam("id") Long id) {
        return feedbackService.deleteFeedback(id);
    }
}
