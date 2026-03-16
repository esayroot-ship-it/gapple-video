package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface seriesService {
    R getAllSeries();

    R getSeriesByTitle(String title);

    R getSeriesByCategory(Long categoryId);

    R getSeriesById(Long id);

}
