package com.ap.apservice.controller;

import com.ap.apcommon.entity.user.history;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.historyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户-观看历史", description = "用户端观看历史相关接口")
@RestController
@RequestMapping("/u/history")
public class historyCon {

    @Autowired
    private historyService historyService;

    @Operation(summary = "获取用户观看历史")
    @GetMapping("/list")
    public R<List<history>> getHistorysByUserId(@RequestParam Long userId) {
        return historyService.getHistorysByUserId(userId);
    }

    @Operation(summary = "添加观看历史")
    @PostMapping("/add")
    public R<Void> addHistory(@RequestParam Long userId,
                        @RequestParam Long videoId) {
        return historyService.addHistory(userId, videoId);
    }
}
