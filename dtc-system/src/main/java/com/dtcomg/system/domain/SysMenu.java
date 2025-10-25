package com.dtcomg.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Menu/Permission entity, maps to sys_menu table.
 */
@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long menuId;
    private Long parentId;
    private String menuName;
    private String path; // Route path
    private String component; // Component path
    private String perms; // Permission identifier
    private String menuType; // M=Directory, C=Menu, F=Button
    private String icon;
    private Integer orderNum;
    private String status; // 0=active, 1=disabled
    private String isShow; // 1=show, 0=hide

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("del_flag")
    private String delFlag;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();
}
