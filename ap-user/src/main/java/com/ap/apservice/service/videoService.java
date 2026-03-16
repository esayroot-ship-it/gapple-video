package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface videoService {

    R getVideosByCategory(Long categoryId);

    R getVideoById(Long id);

    R getVideoBySeries(String seriesId);

    R getVideoByTitle(String title);
}
