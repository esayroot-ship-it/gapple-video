package com.ap.apadmin.service.impl;

import com.ap.apadmin.dao.adminMapper;
import com.ap.apadmin.service.AdminService;
import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;
import com.ap.apcommon.tools.UserCacheUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceIm implements AdminService {

    @Autowired
    public adminMapper adminMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public R createAdmin(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setIsadmin(1); // 设置为管理员
        // 使用 MyBatis-Plus 的 insert 方法
        int result = adminMapper.insert(user);
        if (result > 0) {
            String key = UserCacheUtils.getKey(user.getId());
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.DAYS);
            return R.ok("创建成功");
        }
        return R.fail("创建失败");
    }

    @Transactional
    @Override
    public R deleteAdmin(String username) {
        // 先查询用户获取ID以便删除缓存
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = adminMapper.selectOne(wrapper);

        int result = adminMapper.delete(wrapper);
        if (result > 0) {
            if (user != null) {
                redisTemplate.delete(UserCacheUtils.getKey(user.getId()));
            }
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }

    @Transactional
    @Override
    public R adminLogin(String username, String password) {
        // 构建查询条件：用户名 + 密码 + 是否管理员
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getPassword, password)
                .eq(User::getIsadmin, 1);

        User user = adminMapper.selectOne(wrapper);
        if (user != null) {
            // 登录成功，刷新/回写缓存
            String key = UserCacheUtils.getKey(user.getId());
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.DAYS);
            return R.ok("登录成功");
        } else {
            return R.fail("登录失败");
        }
    }

    @Transactional
    @Override
    public R getallAdmin() {
        List<User> list = adminMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getIsadmin, 1));
        return R.ok("查询成功", list);
    }

}
