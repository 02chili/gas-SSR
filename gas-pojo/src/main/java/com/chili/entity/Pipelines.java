package com.chili.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class Pipelines {
    //管道信息实体类
    @JsonProperty("pipeline_id")
    private Integer pipelineId; //主键id
    @JsonProperty("start_node_id")
    private Integer startNodeId; //开始节点的id
    @JsonProperty("end_node_id")
    private Integer endNodeId; //结束节点的id;
    @JsonProperty("material")
    private String material; //管道材质
    @JsonProperty("burial_depth")
    private BigDecimal burialDepth;//管道埋深
    @JsonProperty("outer_diameter")
    private BigDecimal outerDiameter;//管道外径
    @JsonProperty("pressure")
    private Double pressure;//管道压力
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;
    @JsonProperty("region")
    private String region; // 区域信息

}
