package com.ap.apcommon.entity.user;

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
@Schema(name = "subscribe",description = "订阅表")
@TableName("subscribe")
public class subsrcibe {
    @TableId(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private Integer uid;

    @TableField(value = "series_id")
    private Integer sid;

    @TableField(value = "status")
    private Byte status;

    @TableField(value = "created_at")
    private LocalDateTime createdtime;
}
