package com.ap.apadmin.service;

import com.ap.apcommon.entity.announcement;
import com.ap.apcommon.tools.R;

public interface announcementService {
    R createAnnouncement(announcement announcement);

    R deleteAnnouncement(Long id);

    R updateAnnouncement(announcement announcement);

    R getAnnouncement(Long id);

    R getallAnnouncement();

    R updateStatus(Long id, Integer status);

}
