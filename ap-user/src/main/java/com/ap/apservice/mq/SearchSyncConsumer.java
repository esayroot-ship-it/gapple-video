package com.ap.apservice.mq;

import com.ap.apcommon.dto.SeriesSearchDTO;
import com.ap.apcommon.dto.VideoSearchDTO;
import com.ap.apcommon.entity.video.Video;
import com.ap.apcommon.entity.video.series;
import com.ap.apcommon.tools.PinYinUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SearchSyncConsumer {

    @Autowired
    private Client meiliClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听视频变更消息，增量同步到 Meilisearch
     */
    @RabbitListener(queues = "video.sync.queue")
    public void syncVideo(Video video) throws Exception {
        if (video.getStatus() == 1) {
            VideoSearchDTO dto = new VideoSearchDTO(
                    video.getId().toString(),
                    video.getTitle(),
                    video.getDescription(),
                    video.getCoverUrl(),
                    PinYinUtils.toPinyin(video.getTitle())
            );
            meiliClient.index("videos").addDocuments(objectMapper.writeValueAsString(Collections.singletonList(dto)));
        } else {
            // 下架则删除索引
            meiliClient.index("videos").deleteDocument(video.getId().toString());
        }
    }

    /**
     * 监听剧集变更消息，增量同步到 Meilisearch
     */
    @RabbitListener(queues = "series.sync.queue")
    public void syncSeries(series series) throws Exception {
        if (series.getStatus() == 1) {
            SeriesSearchDTO dto = new SeriesSearchDTO(
                    series.getId().toString(),
                    series.getTitle(),
                    series.getDescription(),
                    series.getCoverUrl(),
                    PinYinUtils.toPinyin(series.getTitle())
            );
            meiliClient.index("series").addDocuments(objectMapper.writeValueAsString(Collections.singletonList(dto)));
        } else {
            meiliClient.index("series").deleteDocument(series.getId().toString());
        }
    }
}
