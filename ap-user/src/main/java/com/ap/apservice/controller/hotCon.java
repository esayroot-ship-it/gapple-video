package com.ap.apservice.controller;

import com.ap.apcommon.entity.hot;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.impl.hotIm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户-热搜管理", description = "用户端热搜查看接口")
@RestController("user-hot")
@RequestMapping("/u/hot")
public class hotCon {

    @Autowired
    private hotIm hotIm;

    @Operation(summary = "获取热搜榜单")
    @GetMapping("/getHotList")
    public R<List<hot>> getHotList () {
        return hotIm.getHotList();
    }

}
