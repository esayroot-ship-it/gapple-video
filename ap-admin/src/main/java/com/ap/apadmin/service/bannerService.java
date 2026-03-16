package com.ap.apadmin.service;

import com.ap.apcommon.entity.banner;
import com.ap.apcommon.tools.R;

public interface bannerService {
    R createBanner(banner banner);

    R deleteBanner(Integer id);

    R updateBanner(banner banner);

    R getBanner(Integer id);

    R getallBanner();

    R updateStatus(Integer id, Integer status);
}
