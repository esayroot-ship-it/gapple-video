package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface episodeService {
    R getEpisodesBySeriesId(Long seriesId);

    R getSeriesByVideoId(Long videoId);
}
