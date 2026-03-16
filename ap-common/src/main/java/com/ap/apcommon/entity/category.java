package com.ap.apcommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "category", description = "分类")
@TableName("category")
public class category {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "parent_id")
    private Integer parentId;

    @TableField(value = "sort")
    private Integer sort;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "created_at")
    private String createdAt;

    @TableField(value = "updated_at")
    private String updatedAt;
}
