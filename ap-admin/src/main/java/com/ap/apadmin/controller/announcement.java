package com.ap.apadmin.controller;

import com.ap.apadmin.service.announcementService;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "公告管理", description = "公告管理相关接口")
@RestController
@RequestMapping("/announcement")
public class announcement {

    @Autowired
    private announcementService service;

    @Operation(summary = "创建公告")
    @PostMapping("/create")
    public R<Void> createAnnouncement(@RequestBody com.ap.apcommon.entity.announcement announcement){
        return service.createAnnouncement(announcement);
    }

    @Operation(summary = "删除公告")
    @PostMapping("/delete")
    public R<Void> deleteAnnouncement(@RequestParam("id") Long id){
        return service.deleteAnnouncement(id);
    }

    @Operation(summary = "更新公告")
    @PostMapping("/update")
    public R<Void> updateAnnouncement(@RequestBody com.ap.apcommon.entity.announcement announcement){
        return service.updateAnnouncement(announcement);
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/get")
    public R<com.ap.apcommon.entity.announcement> getAnnouncement(@RequestParam("id") Long id){
        return service.getAnnouncement(id);
    }

    @Operation(summary = "获取所有公告")
    @GetMapping("/all")
    public R<List<com.ap.apcommon.entity.announcement>> getAllAnnouncement(){
        return service.getallAnnouncement();
    }

    @Operation(summary = "更新公告状态")
    @PostMapping("/updateStatus")
    public R<Void> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return service.updateStatus(id, status);
    }
}
