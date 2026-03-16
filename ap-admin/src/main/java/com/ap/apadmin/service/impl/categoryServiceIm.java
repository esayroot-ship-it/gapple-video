package com.ap.apadmin.service.impl;

import com.ap.apcommon.dao.categoryMapper;
import com.ap.apadmin.service.categoryService;
import com.ap.apcommon.entity.category;
import com.ap.apcommon.tools.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class categoryServiceIm implements categoryService {

    @Autowired
    private categoryMapper categoryMapper;

    @Override
    @Transactional
    public R createCategory(category category) {
        int rows = categoryMapper.insert(category);
        return rows > 0 ? R.ok("分类创建成功") : R.fail("创建失败");
    }

    @Override
    public R deleteCategory(Integer id) {
        int rows = categoryMapper.deleteById(id);
        return rows > 0 ? R.ok("分类删除成功") : R.fail("删除失败");
    }

    @Override
    @Transactional
    public R updateCategory(category category) {
        int rows = categoryMapper.updateById(category);
        return rows > 0 ? R.ok("分类更新成功") : R.fail("更新失败");
    }

    @Override
    public R getCategory(Integer id) {
        category category = categoryMapper.selectById(id);
        return category != null ? R.ok("查询成功", category) : R.fail("未找到该分类");
    }

    @Override
    public R getAllCategory() {
        List<category> list = categoryMapper.selectList(null);
        return list != null ? R.ok("查询成功", list) : R.fail("未找到分类");
    }

}
