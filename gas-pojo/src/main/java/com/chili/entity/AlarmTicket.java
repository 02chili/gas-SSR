package com.chili.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlarmTicket implements Serializable {

    private Integer id;

    private String eventType;

    private LocalDateTime occurrenceTime;

    private String pipelineMaterial;

    private BigDecimal pipelineDepth;

    private BigDecimal pipelineDiameter;

    private BigDecimal pipelinePressure;

    private Integer populationDensity;

    private Integer casualties;

    private String nearbyArea;

    private BigDecimal leakRate;

    private String airflowDirection;

    private Object responseLevel;

    private Integer pipelineId;
}
