package com.ap.apadmin.service;

import com.ap.apcommon.tools.R;

public interface feedbackService {

    R getAllFeedback();

    R updateStatus(Long id, String status);

    R deleteFeedback(Long id);
}
