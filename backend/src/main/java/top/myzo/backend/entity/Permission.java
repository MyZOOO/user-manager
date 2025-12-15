package top.myzo.backend.entity;

import java.util.Date;

public class Permission {
    private Long Id;
    private Long name;
    private String code;
    private String type;
    private String path;
    private String method;
    private Integer status;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}
