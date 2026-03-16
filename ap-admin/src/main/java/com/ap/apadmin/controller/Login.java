package com.ap.apadmin.controller;

import com.ap.apadmin.service.AdminService;
import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员认证", description = "管理员登录及管理相关接口")
@RestController
@RequestMapping("/admin")
public class Login {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "管理员登录")
    @GetMapping("/login")
// 使用 User 对象作为参数会更安全，但这里先优化返回类型
    public R<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return adminService.adminLogin(username, password);
    }

    @Operation(summary = "创建管理员")
    @PostMapping("/create")
    public R<Void> create(@RequestParam("username") String username, @RequestParam("password") String password) {
        return adminService.createAdmin(username, password);
    }

    @Operation(summary = "删除管理员")
    @PostMapping("/delete")
    public R<Void> delete(@RequestParam("username") String username) {
        return adminService.deleteAdmin(username);
    }

    @Operation(summary = "获取所有管理员")
    @GetMapping("/all")
    public R<List<User>> all() {
        return adminService.getallAdmin();
    }
}
