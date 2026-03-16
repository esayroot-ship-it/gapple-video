package com.ap.apcommon.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;


import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "user",description = "用户表")
@TableName(value = "user")
public class User {

    @TableId(value = "id")
    private Long id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password_hash")
    private String password;

    @TableField(value = "nickname")
    private String nickname;

    @TableField(value = "avatar_url")
    private String avatarUrl;

    @TableField(value = "is_admin")
    private Integer isadmin;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "created_at")
    private LocalDateTime createdTime;

    @TableField(value = "updated_at")
    private LocalDateTime updatedTime;

    @Schema(description = "用户头像文件", type = "string", format = "binary")
    @TableField(exist = false)
    private MultipartFile file;
}
