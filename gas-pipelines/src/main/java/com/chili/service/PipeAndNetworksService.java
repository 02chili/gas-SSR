package com.chili.service;


import com.chili.entity.Nodes;
import com.chili.entity.Pipelines;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PipeAndNetworksService {
    //List<Nodes> getNodesByRange(BigDecimal latitude, BigDecimal longitude, BigDecimal radius);
    

    List<Nodes> getAllNodes();

    Nodes getNodeById(int nodeId);

    List<Pipelines> getAllPipelines();

    Pipelines getPipelinesByid(int pipelineId);

    List<Pipelines> getPipelinesByNodeId(int nodeId);

    List<Pipelines> getPipelinesByRegion(String region);

    List<Map<String, Object>> getNodesByRange(Double latitude, Double longitude, Double radius);
}
