package com.ap.apcommon.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_token")
public class usertoken {
    @TableId(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private Integer uid;

    @TableField(value = "token")
    private String token;

    @TableField(value = "expire_at")
    private String expire;

    @TableField(value = "created_at")
    private String created;
}
