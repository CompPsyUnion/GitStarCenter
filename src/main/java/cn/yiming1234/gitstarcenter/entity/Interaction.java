package cn.yiming1234.gitstarcenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("interactions")
public class Interaction {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer sourceUserId;
    private Integer targetUserId;
    private Boolean isStar;
    private Boolean isFork;
    private Boolean isFollow;
    private Boolean isWatch;
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt;
}