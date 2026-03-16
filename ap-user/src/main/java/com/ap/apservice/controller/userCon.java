package com.ap.apservice.controller;

import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;
import com.ap.apservice.service.userService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户-认证管理", description = "用户端注册登录相关接口")
@RestController("user-userCon")
@RequestMapping("/u")
public class userCon {

    @Autowired
    private userService userService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userService.register(username, password);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userService.login(username, password);
    }

    @Operation(summary = "设置昵称")
    @PostMapping("/setNickname")
    public R<User> setNickname(@RequestParam("nickname") String nickname, @RequestParam("username") String username) {
        return userService.setnickname(nickname, username);
    }

    @Operation(summary = "设置用户头像")
    @PostMapping("/setuserimg")
    public R setuserimg(@RequestParam("id") Long id,@RequestParam("file") MultipartFile file) {
        return userService.setuserimg(id, file);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/getById")
    public R<User> getById(@RequestParam("userId") Long id) {
        return userService.getById(id);
    }
}
