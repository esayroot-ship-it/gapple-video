package com.ap.apadmin.controller;

import com.ap.apadmin.service.hotService;
import com.ap.apcommon.entity.hot;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "热搜管理", description = "热搜管理相关接口")
@RestController("/hotCon")
@RequestMapping("/hot")
public class hotCon {

    @Autowired
    private hotService hotService;

    @Operation(summary = "获取所有热搜")
    @GetMapping("/all")
    public R<List<hot>> getAllHot() {
        return hotService.getAllHot();
    }

    @Operation(summary = "添加热搜")
    @PostMapping("/add")
// 修改为使用对象接收，更规范
    public R<Void> addHot(@RequestBody hot hot) {
        return hotService.addHot(hot.getWord(), hot.getSort());
    }

    @Operation(summary = "删除热搜")
    @PostMapping("/delete")
    public R<Void> deleteHot(@RequestParam("id") Integer id) {
        return hotService.deleteHot(id);
    }
}
