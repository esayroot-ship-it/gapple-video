package com.ap.apadmin.service;

import com.ap.apcommon.tools.R;

public interface hotService {
    R getAllHot();

    R addHot(String word ,Integer sort);

    R deleteHot(Integer id);
}
