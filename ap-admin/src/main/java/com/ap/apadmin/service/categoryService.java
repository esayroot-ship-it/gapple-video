package com.ap.apadmin.service;

import com.ap.apcommon.entity.category;
import com.ap.apcommon.tools.R;

public interface categoryService {
    R createCategory(category category);

    R deleteCategory(Integer id);

    R updateCategory(category category);

    R getCategory(Integer id);

    R getAllCategory();


}
