package com.chili.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginVO implements Serializable {
    private Integer id;
    private String username;
    private String token;

}
