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
@Schema(name = "comment",description = "评论")
@TableName("comment")
public class comment {
    @TableId(value = "id")
    private Long id;

    @TableField(value = "video_id")
    private Integer vid;

    @TableField(value = "user_id")
    private Integer uid;

    @TableField(value = "content")
    private String content;

    @TableField(value = "like_count")
    private Byte likecount;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "created_at")
    private LocalDateTime createdat;

}
