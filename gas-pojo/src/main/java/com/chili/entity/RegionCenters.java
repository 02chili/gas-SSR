package com.chili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionCenters implements Serializable {

    private Integer centerId;

    private BigDecimal centerLatitude;

    private BigDecimal centerLongitude;

    private BigDecimal regionRadius;
    private String description;
}
