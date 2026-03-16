package com.ap.apadmin.controller;

import com.ap.apadmin.service.feedbackService;
import com.ap.apcommon.entity.feedback;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "反馈管理", description = "反馈管理相关接口")
@RestController
@RequestMapping("/feedback")
public class feedbackCon {

    @Autowired
    private feedbackService service;

    @Operation(summary = "获取所有反馈")
    @GetMapping("/all")
    public R<List<feedback>> getAllFeedback() {
            return service.getAllFeedback();
    }
    @Operation(summary = "更新反馈状态")
    @PostMapping("/updateStatus")
    public R<Void> updateStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
            return service.updateStatus(id, status);
    }

    @Operation(summary = "删除反馈")
    @PostMapping("/delete")
    public R<Void> delete(@RequestParam("id") Long id) {
            return service.deleteFeedback(id);
    }
}