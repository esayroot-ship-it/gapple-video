package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.categoryMapper;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.categoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("user-categoryService")
public class categoryIm implements categoryService{
    @Autowired
    private categoryMapper categoryMapper;

    @Override
    public R getallCategory() {
        return R.ok("成功", categoryMapper.selectList(null));
    }

}
