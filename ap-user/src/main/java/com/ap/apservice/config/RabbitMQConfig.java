package com.ap.apservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "feedback.exchange";
    public static final String QUEUE_NAME = "feedback.queue";
    public static final String ROUTING_KEY = "feedback.save";

    public static final String VIDEO_SYNC_QUEUE = "video.sync.queue";
    public static final String SERIES_SYNC_QUEUE = "series.sync.queue";

    @Bean
    public DirectExchange feedbackExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue feedbackQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue videoSyncQueue() {
        return new Queue(VIDEO_SYNC_QUEUE, true);
    }

    @Bean
    public Queue seriesSyncQueue() {
        return new Queue(SERIES_SYNC_QUEUE, true);
    }

    @Bean
    public Binding feedbackBinding(Queue feedbackQueue, DirectExchange feedbackExchange) {
        return BindingBuilder.bind(feedbackQueue).to(feedbackExchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding videoSyncBinding(Queue videoSyncQueue, DirectExchange feedbackExchange) {
        return BindingBuilder.bind(videoSyncQueue).to(feedbackExchange).with("video.sync");
    }

    @Bean
    public Binding seriesSyncBinding(Queue seriesSyncQueue, DirectExchange feedbackExchange) {
        return BindingBuilder.bind(seriesSyncQueue).to(feedbackExchange).with("series.sync");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
