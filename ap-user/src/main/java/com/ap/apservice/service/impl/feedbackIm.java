package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.feedbackMapper;
import com.ap.apcommon.entity.feedback;
import com.ap.apcommon.tools.R;
import com.ap.apcommon.dto.FeedbackMessage;
import com.ap.apservice.config.RabbitMQConfig;
import com.ap.apservice.service.feedbackService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service("user-feedbackIm")
public class feedbackIm implements feedbackService {

    @Autowired
    private feedbackMapper feedbackMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public R getFeedbacksByUserId(Long userId) {
        LambdaQueryWrapper<feedback> qw = new LambdaQueryWrapper<>();
        qw.eq(feedback::getUid, userId.intValue());
        return R.ok("成功", feedbackMapper.selectList(qw));
    }

    @Override
    public R addFeedback(Long userId,  String content,String type) {
        // 封装消息对象
        FeedbackMessage message = new FeedbackMessage(
                userId,
                content,
                type,
                LocalDateTime.now()
        );

        // 发送消息到 MQ
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        return R.ok("提交成功，感谢您的反馈！");
    }

    @Override
    public R deleteFeedback(Long feedbackId) {
        return feedbackMapper.deleteById(feedbackId) > 0 ? R.ok("成功") : R.fail("失败");
    }
}
