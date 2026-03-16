package com.ap.apcommon.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "history",description = "播放历史")
@TableName("play_history")
public class history {
    @TableId(value = "id")
    private Integer id;

    @TableField(value = "user_id")
    private Integer uid;

    @TableField(value = "video_id")
    private Integer vid;

    @TableField(value = "progress_seconds")
    private Integer seconds;

    @TableField(value = "last_play_time")
    private String lasttime;
}
