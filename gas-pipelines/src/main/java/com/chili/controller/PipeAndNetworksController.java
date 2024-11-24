package com.chili.controller;

import com.chili.entity.Nodes;
import com.chili.entity.Pipelines;
import com.chili.service.PipeAndNetworksService;
import com.chili.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pipelines")
@Slf4j
@RequiredArgsConstructor
public class PipeAndNetworksController {
    private final PipeAndNetworksService pipeAndNetworksService;

    /**
     * -- 1. 获取所有节点信息
     * --    GET /api/nodes
     * --    返回所有节点的信息，包括节点ID、经纬度、创建时间和更新时间。
     *
     * -- 2. 获取特定节点的详细信息
     * --    GET /api/nodes/{node_id}
     * --    根据节点ID获取节点的详细信息，包括经纬度、创建时间和更新时间。
     *
     * -- 3. 获取所有管道信息
     * --    GET /api/pipelines
     * --    返回所有管道的信息，包括管道ID、起始节点、终止节点、材质、埋深、外径、压力、创建时间和更新时间。
     *
     * -- 4. 获取与特定节点相关的管道
     * --    GET /api/nodes/{node_id}/pipelines
     * --    根据节点ID获取与该节点相关的所有管道信息，包括起始节点、终止节点、材质等。
     *
     * -- 5. 获取特定管道的详细信息
     * --    GET /api/pipelines/{pipeline_id}
     * --    根据管道ID获取管道的详细信息，包括起始节点、终止节点、材质、埋深、外径、压力等。
     *
     * -- 6. 搜索附近的节点
     * --    GET /api/nodes/nearby?latitude={latitude}&longitude={longitude}&radius={radius}
     * --    根据提供的经纬度和半径，返回指定范围内的所有节点。
     */
    @GetMapping("/nodes")
    public Result<List<Nodes>> getAllNodes() {
        log.info("获取所有节点信息");
        List<Nodes> res = pipeAndNetworksService.getAllNodes();
        return Result.success(res);
    }
    @GetMapping("/nodes/{nodeId}")
    public Result<Nodes> getNodeById(int nodeId) {
        log.info("根据节点id获取节点具体信息");
        Nodes nodes = pipeAndNetworksService.getNodeById(nodeId);
        return Result.success(nodes);
    }
    @GetMapping("/pipelines")
    public Result<List<Pipelines>> getAllPipelines() {
        log.info("获取所有管道信息");
        List<Pipelines> res = pipeAndNetworksService.getAllPipelines();
        return Result.success(res);
    }
    @GetMapping("/pipelines/{pipelineId}")
    public Result<Pipelines> getPipelinesByid(int pipelineId) {
        log.info("根据id获取对应的管道信息");
        Pipelines pipelines = pipeAndNetworksService.getPipelinesByid(pipelineId);
        return Result.success(pipelines);
    }
    @GetMapping("/nodes/{nodeId}/pipelines")
    public Result<List<Pipelines>> getPipelinesByNodeId(int nodeId) {
        log.info("获取和节点信息相关的管道信息");
        List<Pipelines> res= pipeAndNetworksService.getPipelinesByNodeId(nodeId);
        return Result.success(res);
    }
    /*6. 搜索附近的节点
     * --    GET /api/nodes/nearby?latitude={latitude}&longitude={longitude}&radius={radius}
     * --    根据提供的经纬度和半径，返回指定范围内的所有节点。*/
    /*@GetMapping("/nodes/nearby/{latitude}/{longitude}/{radius}")
    @Cacheable(cacheNames = "nodesCache", key = "#latitude.toPlainString() + '_' + #longitude.toPlainString() + '_' + #radius.toPlainString()")
    public Result<List<Nodes>> getNodesByRange(  @PathVariable BigDecimal latitude,
                                                 @PathVariable BigDecimal longitude,
                                                 @PathVariable BigDecimal radius){
        log.info("查询附近范围内的节点, 纬度: {}, 经度: {}, 半径: {}", latitude, longitude, radius);
        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0 || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new OutRangeException("纬度必须在 -90 到 90 之间");
        }
        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0 || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new OutRangeException("经度必须在 -180 到 180 之间");
        }
        if (radius.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OutRangeException("半径必须是正数");
        }
        List<Nodes> res = pipeAndNetworksService.getNodesByRange(latitude,longitude,radius);
        return Result.success(res);
    }*/
    @GetMapping("/nearby/{latitude}/{longitude}/{radius}")
    public Result<List<Map<String, Object>>> getNodesByRange(
            @PathVariable("latitude") Double latitude,
            @PathVariable("longitude") Double longitude,
            @PathVariable("radius") Double radius) {
        try {
            List<Map<String, Object>> nodes = pipeAndNetworksService.getNodesByRange(latitude, longitude, radius);
            return Result.success(nodes);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    @GetMapping("/{region}")
    public Result<List<Pipelines>> getPipelinesByRegion(@PathVariable String region) {
        List<Pipelines> list = pipeAndNetworksService.getPipelinesByRegion(region);
        return Result.success(list);
    }
}
