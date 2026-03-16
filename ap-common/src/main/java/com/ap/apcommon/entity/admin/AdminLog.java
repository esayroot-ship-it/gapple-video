package com.ap.apcommon.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("admin_op_log")
public class AdminLog {
    @TableField(value = "id")
    private Long id;

    @TableField(value = "admin_user_id")
    private Integer adminId;

    @TableField(value = "ip")
    private String ip;

    @TableField(value = "user_agent")
    private String useragent;

    @TableField(value = "login_time")
    private LocalDateTime logintime;

    @TableField(value = "success")
    private Byte success;

    @TableField(value = "file_reason")
    private String message;

}
