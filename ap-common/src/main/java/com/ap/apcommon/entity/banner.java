package com.ap.apcommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "banner", description = "轮播图")
@TableName("banner")
public class banner {
    @TableId(value = "id")
    private Integer id;

    @TableField(value = "title")
    private String title;

    @TableField(value = "image_url")
    private String imgurl;

    @TableField(value = "target_type")
    private String type;

    @TableField(value = "target_video_id")
    private Integer tvid;

    @TableField(value = "target_topic_id")
    private Integer ttid;

    @TableField(value = "target_url")
    private String turl;

    @TableField(value = "sort")
    private Integer sort;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "created_at")
    private String createdat;
}
