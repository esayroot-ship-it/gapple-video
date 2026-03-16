package com.ap.apservice.controller;

import com.ap.apcommon.tools.R;
import com.ap.apservice.service.tagAndTopic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户-标签与话题", description = "用户端标签与话题查看接口")
@RestController("user-tagCon")
@RequestMapping("/u/tag")
public class tagAndTopicCon {

    @Autowired
    private tagAndTopic tagAndTopic;

    @Operation(summary = "获取标签和话题")
    @GetMapping("/list")
    public R gettagAndTopic(){
        return tagAndTopic.getTagAndTopic();
    }
}
