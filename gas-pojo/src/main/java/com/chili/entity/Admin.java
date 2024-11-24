package com.chili.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "admin")
public class Admin implements Serializable {

    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Integer id;
    private String username;
    private String password;
}
