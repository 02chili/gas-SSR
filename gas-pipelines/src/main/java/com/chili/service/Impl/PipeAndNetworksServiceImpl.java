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
        log.info("开始执行 getNodesByRange 方法。纬度: {}, 经度: {}, 半径: {}", latitude, longitude, radius);

        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        List<Map<String, Object>> result = new ArrayList<>();
        String rangeKey = "geo_key:range";

        try {
            // 查询 Redis 中缓存的范围
            Map<Object, Object> cachedRange = stringRedisTemplate.opsForHash().entries(rangeKey);

            if (cachedRange != null && !cachedRange.isEmpty()) {
                double cachedLatitude = Double.parseDouble((String) cachedRange.getOrDefault("latitude", "0"));
                double cachedLongitude = Double.parseDouble((String) cachedRange.getOrDefault("longitude", "0"));
                double cachedRadius = Double.parseDouble((String) cachedRange.getOrDefault("radius", "0"));

                // 判断当前查询是否在缓存范围内
                boolean isWithinCachedRange = isWithinRange(latitude, longitude, radius, cachedLatitude, cachedLongitude, cachedRadius);
                if (isWithinCachedRange) {
                    log.info("查询范围在缓存范围内，从 Redis 获取附近节点数据。");
                    GeoResults<RedisGeoCommands.GeoLocation<String>> nearbyNodes = geoOps.radius(
                            GEO_KEY,
                            new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS)),
                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
                    );

                    if (nearbyNodes != null && !nearbyNodes.getContent().isEmpty()) {
                        log.info("在 Redis 中找到 {} 个节点数据，返回缓存结果。", nearbyNodes.getContent().size());
                        for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : nearbyNodes.getContent()) {
                            RedisGeoCommands.GeoLocation<String> location = geoResult.getContent();
                            if (location.getPoint() == null) continue;

                            Map<String, Object> node = new HashMap<>();
                            node.put("nodeId", location.getName());
                            node.put("latitude", location.getPoint().getY());
                            node.put("longitude", location.getPoint().getX());
                            result.add(node);
                        }
                        return result;
                    }
                } else {
                    log.info("Redis 中缓存的范围不完全覆盖查询范围，部分数据可能缺失。");
                }
            }
        } catch (Exception e) {
            log.error("查询 Redis 出现错误，回退到数据库查询。", e);
        }

        // 如果范围不匹配或缓存无结果，从数据库查询
        log.info("从数据库查询节点数据。");
        List<Map<String, Object>> dbResult = pipeAndNetworksMapper.getNodesByRange(latitude, longitude, radius);

        if (!dbResult.isEmpty()) {
            log.info("数据库查询返回了 {} 个节点数据，将结果缓存到 Redis 中。", dbResult.size());
            for (Map<String, Object> node : dbResult) {
                geoOps.add(
                        GEO_KEY,
                        new Point((Double) node.get("longitude"), (Double) node.get("latitude")),
                        String.valueOf(node.get("nodeId"))
                );
                // 检查并避免重复缓存
                log.info("缓存节点 ID: {}，经度: {}，纬度: {}", node.get("nodeId"), node.get("longitude"), node.get("latitude"));
            }

            // 更新缓存范围
            Map<String, String> newRange = new HashMap<>();
            newRange.put("latitude", String.valueOf(latitude));
            newRange.put("longitude", String.valueOf(longitude));
            newRange.put("radius", String.valueOf(radius));
            stringRedisTemplate.opsForHash().putAll(rangeKey, newRange);
        } else {
            log.warn("数据库查询未返回任何节点数据！");
        }

        result.addAll(dbResult); // 合并数据库结果
        return result;
    }

    /**
     * 判断新查询范围是否被缓存范围覆盖
     */
    private boolean isWithinRange(Double latitude, Double longitude, Double radius,
                                  Double cachedLatitude, Double cachedLongitude, Double cachedRadius) {
        double distance = calculateDistance(latitude, longitude, cachedLatitude, cachedLongitude);
        return distance + radius <= cachedRadius;
    }

    /**
     * 计算两点之间的直线距离
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // 地球半径，单位：km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 返回距离，单位：km
    }




}
