package com.ap.apcommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "feedback", description = "反馈")
@TableName("feedback")
public class feedback {
    @TableId(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private Integer uid;

    @TableField(value = "type")
    private String type;

    @TableField(value = "content")
    private String content;

    @TableField(value = "contact")
    private String phone;

    @TableField(value = "status")
    private String status;

    @TableField(value = "created_at")
    private LocalDateTime createdtime;
}
