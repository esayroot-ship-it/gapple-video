package com.ap.apcommon.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "commentlike",description = "点赞")
@TableName("comment_like")
public class commentlike {
    @TableId
    private Long id;

    @TableField(value = "comment_id")
    private Integer cid;

    @TableField(value = "user_id")
    private Integer uid;

    @TableField(value = "created_at")
    private LocalDateTime createdtime;
}
