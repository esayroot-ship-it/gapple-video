package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface historyService {
    R getHistorysByUserId(Long userId);

    R addHistory(Long userId, Long videoId);
}
