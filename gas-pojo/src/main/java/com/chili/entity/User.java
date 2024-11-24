package com.chili.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user")
public class User implements Serializable {

    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Integer id;

    private String username;

    private String password;

    private String fullName;

    private String phone;

    private Integer departmentId;

    private String departmentName;

    private Integer status;

}