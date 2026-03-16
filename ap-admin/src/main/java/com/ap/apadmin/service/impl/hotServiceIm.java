package com.ap.apadmin.service.impl;

import com.ap.apadmin.service.hotService;
import com.ap.apcommon.dao.hotMapper;
import com.ap.apcommon.entity.hot;
import com.ap.apcommon.tools.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("hotIm")
public class hotServiceIm implements hotService {

    @Autowired
    private hotMapper hotMapper;

    @Override
    public R getAllHot() {
        QueryWrapper<hot> queryWrapper = new QueryWrapper<>();
        // 按排序权重升序排列
        queryWrapper.orderByAsc("sort");
        return R.ok("查询成功", hotMapper.selectList(queryWrapper));
    }

    @Override
    public R addHot(String word, Integer sort) {
        hot h = new hot();
        h.setWord(word);
        h.setSort(sort);
        // 设置默认状态为启用 (1)
        h.setStatus((byte) 1);
        // 设置创建时间
        h.setCreatedtime(LocalDateTime.now());

        int rows = hotMapper.insert(h);
        return rows > 0 ? R.ok("添加成功") : R.fail("添加失败");
    }

    @Override
    public R deleteHot(Integer id) {
        int rows = hotMapper.deleteById(id);
        return rows > 0 ? R.ok("删除成功") : R.fail("删除失败");
    }

}
