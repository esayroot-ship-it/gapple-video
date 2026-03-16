package com.ap.apservice.service.impl;

import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;
import com.ap.apcommon.tools.UserCacheUtils;
import com.ap.apservice.dao.userMapper2;
import com.ap.apservice.service.userService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service("user-userIm")
public class userIm implements userService {

    @Autowired
    private userMapper2 userMapper2;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${file-storage.user-path}")
    private String uploadDir;

    private static final String URL_PREFIX = "/uimg/";

    @Override
    public R getById(Long id) {
        String key = UserCacheUtils.getKey(id);
        // 1. 查缓存
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            return R.ok("查询成功", user);
        }

        // 2. 查数据库
        user = userMapper2.selectById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 3. 写缓存 (设置过期时间，例如30分钟)
        redisTemplate.opsForValue().set(key, user, 300, TimeUnit.MINUTES);
        return R.ok("查询成功", user);
    }

    @Override
    public R register(String username, String password) {
        // 1. 基础校验
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return R.fail("用户名或密码不能为空");
        }

        // 2. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper2.selectCount(queryWrapper) > 0) {
            return R.fail("该用户名已被注册");
        }

        // 3. 创建用户实体
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 暂时使用明文，建议后续改为加密存储
        user.setNickname("User_" + System.currentTimeMillis()); // 默认昵称
        user.setAvatarUrl(""); // 默认头像为空
        user.setIsadmin(0); // 默认为普通用户
        user.setStatus(1); // 默认状态正常
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        // 4. 保存到数据库
        int rows = userMapper2.insert(user);
        if (rows > 0) {
            // 写入 Redis
            String key = UserCacheUtils.getKey(user.getId());
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.DAYS);
            return R.ok("注册成功");
        }
        return R.fail("注册失败");
    }

    @Override
    public R login(String username, String password) {
        // 1. 基础校验
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return R.fail("用户名或密码不能为空");
        }

        // 2. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper2.selectOne(queryWrapper);

        // 3. 校验用户是否存在及密码是否匹配
        if (user == null || !user.getPassword().equals(password)) {
            return R.fail("用户名或密码错误");
        }

        // 4. 校验账号状态
        if (user.getStatus() != null && user.getStatus() != 1) {
            return R.fail("该账号已被封禁或停用");
        }

        // 5. 登录成功，清除敏感信息后返回
        user.setPassword(null);

        // 刷新/回写缓存，确保缓存中有最新的用户信息
        String key = UserCacheUtils.getKey(user.getId());
        redisTemplate.opsForValue().set(key, user, 1, TimeUnit.DAYS);

        return R.ok("登录成功", user);
    }

    @Override
    public R setnickname(String nickname, String username) {
        // 1. 获取用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper2.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 3. 更新昵称
        user.setNickname(nickname);
        int rows = userMapper2.updateById(user);
        if (rows > 0) {
            redisTemplate.delete(UserCacheUtils.getKey(user.getId()));
            return R.ok("昵称更新成功", user);
        }
        return R.fail("昵称更新失败");
    }

    @Override
    public R setuserimg(Long id , MultipartFile file) {
        // 1. 校验用户
        User user = userMapper2.selectById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 2. 校验文件
        if (file == null || file.isEmpty()) {
            return R.fail("请上传头像文件");
        }

        // 3. 确保目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 4. 保存文件
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + suffix;
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // 删除旧头像文件
            String oldAvatarUrl = user.getAvatarUrl();
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty() && oldAvatarUrl.startsWith(URL_PREFIX)) {
                String oldFileName = oldAvatarUrl.substring(URL_PREFIX.length());
                File oldFile = new File(dir, oldFileName);
                if (oldFile.exists() && oldFile.isFile()) {
                    oldFile.delete();
                }
            }

            // 5. 更新数据库
            user.setAvatarUrl(URL_PREFIX + fileName);
            user.setUpdatedTime(LocalDateTime.now());
            int rows = userMapper2.updateById(user);
            if (rows > 0) {
                // 6. 清除/更新缓存
                redisTemplate.delete(UserCacheUtils.getKey(id));
                return R.ok("头像上传成功", user.getAvatarUrl());
            }
            return R.fail("头像更新失败");
        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("文件上传出错: " + e.getMessage());
        }
    }
}
