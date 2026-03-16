package com.ap.apservice.service.impl;

import com.ap.apcommon.dao.hotMapper;
import com.ap.apcommon.entity.hot;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.hotService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("user-hotIm")
public class hotIm implements hotService {

    @Autowired
    private hotMapper hotMapper;

    @Override
    public R getHotList() {
        QueryWrapper<hot> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        return R.ok("成功", hotMapper.selectList(queryWrapper));
    }

}
