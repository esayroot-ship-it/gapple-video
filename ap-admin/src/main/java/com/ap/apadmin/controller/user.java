package com.ap.apadmin.controller;

import com.ap.apadmin.service.userService;
import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
public class user {

    @Autowired
    private userService userservice;

    @Operation(summary = "根据用户名获取用户(登录校验)")
    @GetMapping("/getByName")
    public R<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userservice.getUserByUsername(username);
    }

    /**
     * 创建用户
     * 修改为 @RequestParam 以支持文件上传和文档工具识别
     */
    @Operation(summary = "创建用户(含头像)")
    @PostMapping("/create")
    public R<Void> createUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "isadmin", defaultValue = "0") Integer isadmin,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setIsadmin(isadmin);
        user.setStatus(status);
        user.setFile(file); // 将文件放入对象传递给 Service

        return userservice.createUser(user);
    }

    @Operation(summary = "删除用户")
    @PostMapping("/delete")
    public R<Void> deleteUser(@RequestParam("id") Long id) {
        return userservice.deleteUser(id);
    }

    @Operation(summary = "根据用户名删除用户")
    @PostMapping("/deleteByName")
    public R<Void> deleteUserByUsername(@RequestParam("username") String username) {
        return userservice.deleteUserByname(username);
    }

    /**
     * 更新用户
     * 修改为 @RequestParam 以支持文件上传和文档工具识别
     */
    @Operation(summary = "更新用户(含头像)")
    @PostMapping("/update")
    public R<Void> updateUser(
            @RequestParam("id") Long id,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "isadmin", required = false) Integer isadmin,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setIsadmin(isadmin);
        user.setStatus(status);
        user.setFile(file);

        return userservice.updateUser(user);
    }

    // save 方法如果用于前端 JSON 提交可保留，如果也需要文件上传建议参考 create/update 修改或废弃
    @Operation(summary = "保存用户(新增或更新)")
    @PostMapping("/save")
    public R<Void> save(@RequestBody User user) {
        if (user.getId() != null) {
            return userservice.updateUser(user);
        } else {
            return userservice.createUser(user);
        }
    }

    @Operation(summary = "根据用户名获取用户")
    @GetMapping("/getByUsername")
    public R<User> getUserByUsername(@RequestParam("username") String username) {
        return userservice.getUserByUsername(username);
    }

    @Operation(summary = "获取所有用户")
    @GetMapping("/all")
    public R<List<User>> getallUser() {
        return userservice.getallUser();
    }

    @Operation(summary = "封禁用户")
    @PostMapping("/ban")
    public R<Void> banUser(@RequestParam("username") String username) {
        return userservice.banUserByUsername(username);
    }

    @Operation(summary = "解封用户")
    @PostMapping("/reban")
    public R<Void> rebanUser(@RequestParam("username") String username) {
        return userservice.rebanUserByUsername(username);
    }
}