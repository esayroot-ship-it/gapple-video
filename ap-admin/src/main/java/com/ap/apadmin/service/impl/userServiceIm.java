package com.ap.apadmin.service.impl;

import com.ap.apadmin.dao.userMapper;
import com.ap.apadmin.service.userService;
import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;
import com.ap.apcommon.tools.UserCacheUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class userServiceIm implements userService {

    @Autowired
    private userMapper usermapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 头像上传保存路径 (注意结尾的反斜杠)
    private static final String UPLOAD_DIR = "D:\\code\\apvideos\\userimg\\";
    // 头像访问 URL 前缀
    private static final String URL_PREFIX = "/userimg/";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createUser(User user) {
        // 1. 处理头像上传
        if (user.getFile() != null && !user.getFile().isEmpty()) {
            String fileName = uploadFile(user.getFile());
            if (fileName != null) {
                user.setAvatarUrl(URL_PREFIX + fileName);
            }
        }

        // 2. 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        // 3. 插入数据库
        int rows = usermapper.insert(user);
        if (rows > 0) {
            // 写入 Redis
            String key = UserCacheUtils.getKey(user.getId());
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.DAYS);
            return R.ok("用户创建成功");
        }
        return R.fail("创建失败");
    }

    @Override
    public R getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = usermapper.selectOne(wrapper);
        return user != null ? R.ok("查询到用户", user) : R.fail("未找到该用户");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateUser(User user) {
        // 1. 如果有新文件上传，先处理文件
        if (user.getFile() != null && !user.getFile().isEmpty()) {
            // (可选) 查询旧用户信息以删除旧头像，避免垃圾文件堆积
            User oldUser = usermapper.selectById(user.getId());
            if (oldUser != null && oldUser.getAvatarUrl() != null) {
                deleteFile(oldUser.getAvatarUrl());
            }

            // 上传新头像
            String fileName = uploadFile(user.getFile());
            if (fileName != null) {
                user.setAvatarUrl(URL_PREFIX + fileName);
            }
        }

        // 2. 更新数据库
        int rows = usermapper.updateById(user);
        if (rows > 0) {
            redisTemplate.delete(UserCacheUtils.getKey(user.getId()));
            return R.ok("用户信息更新成功");
        }
        return R.fail("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteUser(Long id) {
        // 1. 查询用户以获取头像路径
        User user = usermapper.selectById(id);

        // 2. 如果用户存在且有头像，删除物理文件
        if (user != null && user.getAvatarUrl() != null) {
            deleteFile(user.getAvatarUrl());
        }

        // 3. 删除数据库记录
        int rows = usermapper.deleteById(id);
        if (rows > 0) {
            redisTemplate.delete(UserCacheUtils.getKey(id));
            return R.ok("用户已删除");
        }
        return R.fail("删除失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteUserByname(String username) {
        // 1. 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = usermapper.selectOne(wrapper);

        // 2. 删除文件
        if (user != null && user.getAvatarUrl() != null) {
            deleteFile(user.getAvatarUrl());
        }

        // 3. 删除数据库记录
        int rows = usermapper.delete(wrapper);
        if (rows > 0) {
            if (user != null) {
                redisTemplate.delete(UserCacheUtils.getKey(user.getId()));
            }
            return R.ok("用户 " + username + " 已被成功删除");
        }
        return R.fail("未找到该用户或删除失败");
    }

    @Override
    public R getallUser() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsadmin, 0);
        List<User> list = usermapper.selectList(queryWrapper);
        return R.ok("查询成功", list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R banUserByUsername(String username) {
        // 1. 查询用户获取ID
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User existUser = usermapper.selectOne(wrapper);

        if (existUser == null) {
            return R.fail("用户不存在");
        }

        User user = new User();
        user.setStatus(0);
        int rows = usermapper.update(user, wrapper);
        if (rows > 0) {
            redisTemplate.delete(UserCacheUtils.getKey(existUser.getId()));
            return R.ok("用户 " + username + " 已被封禁");
        }
        return R.fail("封禁失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R rebanUserByUsername(String username) {
        // 1. 查询用户获取ID
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User existUser = usermapper.selectOne(wrapper);

        if (existUser == null) {
            return R.fail("用户不存在");
        }

        User user = new User();
        user.setStatus(1);
        int rows = usermapper.update(user, wrapper);
        if (rows > 0) {
            redisTemplate.delete(UserCacheUtils.getKey(existUser.getId()));
            return R.ok("用户 " + username + " 已被解封");
        }
        return R.fail("解封失败");
    }

    /**
     * 文件上传辅助方法
     */
    private String uploadFile(MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 使用原文件名，建议生产环境使用 UUID 防止冲突
            String fileName = file.getOriginalFilename();
            // 防止文件名为空
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "user_" + System.currentTimeMillis() + ".jpg";
            }

            File dest = new File(dir, fileName);
            file.transferTo(dest);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件删除辅助方法
     */
    private void deleteFile(String urlPath) {
        if (urlPath == null || urlPath.isEmpty()) {
            return;
        }
        // 去除 URL 前缀，获取文件名
        String fileName = urlPath.replace(URL_PREFIX, "");
        File file = new File(UPLOAD_DIR + fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
