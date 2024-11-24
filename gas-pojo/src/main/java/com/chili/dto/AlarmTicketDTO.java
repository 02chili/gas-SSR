package com.chili.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AlarmTicketDTO {
    private String eventType;

    private Date occurrenceTime;

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
