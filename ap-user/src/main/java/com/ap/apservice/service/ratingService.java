package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface ratingService {
    R addRating(Long userId, Long videoId, Integer score);

    R deleteRating(Long userId, Long videoId);

    R getUserRating(Long userId, Long videoId);

    R getRatingByVideoId(Long videoId);
}
