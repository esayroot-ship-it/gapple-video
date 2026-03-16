package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface subsrcibeService {
    R addSubscribe(Long userId, Long seriesId);

    R deleteSubscribe(Long userId, Long seriesId);

    R getSubscribes(Long userId);

    R getIfSubscribed(Long userId, Long seriesId);
}
