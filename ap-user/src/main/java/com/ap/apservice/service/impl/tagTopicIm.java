package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.tagMapper;
import com.ap.apcommon.dao.topicMapper;
import com.ap.apcommon.entity.video.Tag;
import com.ap.apcommon.entity.video.Topic;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.tagAndTopic;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("user-tagAndTopic")
public class tagTopicIm implements tagAndTopic {
    @Autowired
    private tagMapper tagMapper;

    @Autowired
    private topicMapper topicMapper;

    @Override
    public R getTagAndTopic() {
        // 1. 获取所有 Tag
        List<Tag> tagList = tagMapper.selectList(null);

        // 2. 获取所有状态正常的 Topic (假设 status=1 为启用)
        QueryWrapper<Topic> topicWrapper = new QueryWrapper<>();
        topicWrapper.eq("status", 1);
        List<Topic> topicList = topicMapper.selectList(topicWrapper);

        // 3. 封装结果
        Map<String, Object> data = new HashMap<>();
        data.put("tags", tagList);
        data.put("topics", topicList);

        return R.ok("查询成功", data);
    }
}
