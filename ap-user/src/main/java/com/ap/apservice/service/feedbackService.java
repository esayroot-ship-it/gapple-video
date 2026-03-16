package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface feedbackService {
    R getFeedbacksByUserId(Long userId);

    R addFeedback(Long userId, String content, String type);

    R deleteFeedback(Long feedbackId);
}
