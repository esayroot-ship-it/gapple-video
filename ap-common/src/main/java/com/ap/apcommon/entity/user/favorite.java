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


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "favorite",description = "收藏")
@TableName("favorite")
public class favorite {
    @TableId(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private  Integer uid;

    @TableField(value = "video_id")
    private  Integer vid;

    @TableField(value = "created_at")
    private LocalDateTime createdtime;
}
