package com.ap.apadmin.service.impl;


import com.ap.apcommon.dao.feedbackMapper;
import com.ap.apadmin.service.feedbackService;
import com.ap.apcommon.entity.feedback;
import com.ap.apcommon.tools.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class feedbackServiceIm implements feedbackService {

    @Autowired
    private feedbackMapper feedbackMapper;

    @Override
    public R getAllFeedback() {
        List<feedback> list = feedbackMapper.selectList(null);
        return list != null ? R.ok("查询成功", list) : R.fail("未找到反馈信息");
    }

    @Override
    public R updateStatus(Long id, String status) {
        feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return R.fail("未找到该反馈");
        }
        feedback.setStatus(status);
        int rows = feedbackMapper.updateById(feedback);
        return rows > 0 ? R.ok("反馈状态更新成功") : R.fail("状态更新失败");
    }

    @Override
    public R deleteFeedback(Long id) {
        int rows = feedbackMapper.deleteById(id);
        return rows > 0 ? R.ok("删除成功") : R.fail("删除失败");
    }
}
