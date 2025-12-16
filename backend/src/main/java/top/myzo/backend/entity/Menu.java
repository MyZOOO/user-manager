package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_menu")
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;          // 菜单名称
    private String path;          // 路由路径
    private String component;     // 前端组件路径
    private String icon;          // 图标
    private Integer sort;         // 排序序号

    @TableField("parent_id")
    private Long parentId;        // 父菜单ID，根为0或null

    @TableField("hidden")
    private Boolean hidden;       // 是否隐藏

    // 逻辑删除
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Menu> children;  // 树结构子菜单（非持久化字段）

    // 显式 getter/setter，避免 Lombok 失效导致的编译问题
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<Menu> getChildren() { return children; }
    public void setChildren(List<Menu> children) { this.children = children; }
}
