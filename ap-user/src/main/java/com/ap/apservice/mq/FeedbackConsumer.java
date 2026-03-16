package com.ap.apservice.mq;

import com.ap.apcommon.dao.feedbackMapper;
import com.ap.apcommon.entity.feedback;
import com.ap.apcommon.dto.FeedbackMessage;
import com.ap.apservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
public class FeedbackConsumer {

    @Autowired
    private feedbackMapper feedbackMapper;

    @RabbitHandler
    public void process(FeedbackMessage msg) {
        feedback fb = new feedback();
        fb.setUid(msg.getUserId().intValue());
        fb.setContent(msg.getContent());
        fb.setType(msg.getType());
        fb.setCreatedtime(msg.getCreatedAt());
        fb.setStatus("0"); 
        
        feedbackMapper.insert(fb);
    }
}
