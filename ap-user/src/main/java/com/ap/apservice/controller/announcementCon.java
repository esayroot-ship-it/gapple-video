package com.ap.apservice.controller;

import com.ap.apcommon.entity.announcement;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.impl.announcementIm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户-公告管理", description = "用户端公告查看接口")
@RestController("user-announcement")
@RequestMapping("/u/announcement")
public class announcementCon {

    @Autowired
    private announcementIm service;

    @Operation(summary = "获取所有公告")
    @GetMapping("/all")
    public R<List<announcement>> getAllAnnouncement(){
        return service.getallAnnouncement();
    }

}
