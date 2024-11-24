package com.chili.service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.chili.entity.Nodes;
import com.chili.entity.Pipelines;
import com.chili.mapper.PipeAndNetworksMapper;
import com.chili.service.PipeAndNetworksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipeAndNetworksServiceImpl implements PipeAndNetworksService {
    private final PipeAndNetworksMapper pipeAndNetworksMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final StringRedisTemplate stringRedisTemplate;
    String GEO_KEY = "gas_pipeline:nodes";
    /*@Override
    public List<Nodes> getNodesByRange(BigDecimal latitude, BigDecimal longitude, BigDecimal radius) {
        RegionCenters regionCenters = pipeAndNetworksMapper.findNearestCenter(latitude,longitude);
        latitude = regionCenters.getCenterLatitude();
        longitude = regionCenters.getCenterLongitude();
        radius = BigDecimal.valueOf(2000);
        List<Nodes> list = pipeAndNetworksMapper.getNodesByRange(latitude,longitude,radius);
        return list;
    }*/


    @Override
    public List<Nodes> getAllNodes() {
        List<Nodes> list = new ArrayList<>();
        list = pipeAndNetworksMapper.getAllNodes();
        return list;
    }

    @Override
    public Nodes getNodeById(int nodeId) {
        Nodes nodes = pipeAndNetworksMapper.getNodeById(nodeId);
        return nodes;
    }

    @Override
    public List<Pipelines> getAllPipelines() {
        List<Pipelines> list = pipeAndNetworksMapper.getAllPipelines();
        return list;
    }

    @Override
    public Pipelines getPipelinesByid(int pipelineId) {
        Pipelines pipelines = pipeAndNetworksMapper.getPipelinesByid(pipelineId);
        return pipelines;
    }

    @Override
    public List<Pipelines> getPipelinesByNodeId(int nodeId) {
        List<Pipelines> list = pipeAndNetworksMapper.getPipelinesByNodeId(nodeId);
        return list;
    }

    @Override
    public List<Pipelines> getPipelinesByRegion(String region) {
        List<Pipelines> pipelinesList = new ArrayList<>();

        try {
            // 执行搜索请求
            SearchResponse<Pipelines> searchResponse = elasticsearchClient.search(s -> s
                    .index("pipelines_index") // 索引名称
                    .query(q -> q
                            .match(t -> t
                                    .field("region")
                                    .query(region)
                            )
                    ), Pipelines.class);
            // 将搜索结果转换为 Pipelines 列表
            for (Hit<Pipelines> hit : searchResponse.hits().hits()) {
                pipelinesList.add(hit.source());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pipelinesList;
    }

    @Override
    public List<Map<String, Object>> getNodesByRange(Double latitude, Double longitude, Double radius) {
        log.info("Starting getNodesByRange. Latitude: {}, Longitude: {}, Radius: {}", latitude, longitude, radius);

        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        List<Map<String, Object>> result = new ArrayList<>();
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS));

        try {
            log.info("Querying Redis for nearby nodes.");
            GeoResults<RedisGeoCommands.GeoLocation<String>> nearbyNodes = geoOps.radius(
                    GEO_KEY,
                    circle,
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
            );

            if (nearbyNodes != null && !nearbyNodes.getContent().isEmpty()) {
                log.info("Found nodes in Redis. Returning cached results.");
                for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : nearbyNodes.getContent()) {
                    RedisGeoCommands.GeoLocation<String> location = geoResult.getContent();
                    if (location.getPoint() == null) {
                        log.warn("Point is null for member: {}", location.getName());
                        continue; // 跳过没有坐标的数据
                    }

                    Map<String, Object> node = new HashMap<>();
                    node.put("nodeId", location.getName());
                    node.put("latitude", location.getPoint().getY());
                    node.put("longitude", location.getPoint().getX());
                    result.add(node);
                }
                return result;
            }
        } catch (Exception e) {
            log.error("Error querying Redis. Falling back to database query.", e);
        }

        // 如果 Redis 查询失败或没有结果，从数据库查询
        log.info("Querying database for nodes.");
        result = pipeAndNetworksMapper.getNodesByRange(latitude, longitude, radius);

        if (!result.isEmpty()) {
            log.info("Database query returned results. Caching results in Redis.");
            for (Map<String, Object> node : result) {
                try {
                    geoOps.add(
                            GEO_KEY,
                            new Point((Double) node.get("longitude"), (Double) node.get("latitude")),
                            String.valueOf(node.get("nodeId"))
                    );
                } catch (Exception e) {
                    log.error("Error caching node in Redis. Node ID: {}", node.get("nodeId"), e);
                }
            }
        }

        return result;
    }


}
