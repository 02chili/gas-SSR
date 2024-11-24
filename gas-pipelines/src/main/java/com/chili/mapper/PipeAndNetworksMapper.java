package com.chili.mapper;


import com.chili.entity.Nodes;
import com.chili.entity.Pipelines;
import com.chili.entity.RegionCenters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface PipeAndNetworksMapper {
    @Select("select * from nodes")
    List<Nodes> getAllNodes();

    @Select("select *from nodes where node_id=#{nodeId}")
    Nodes getNodeById(int nodeId);

    @Select("select *from pipelines")
    List<Pipelines> getAllPipelines();

    @Select("select *from pipelines where pipeline_id = #{pipelineId}")
    Pipelines getPipelinesByid(int pipelineId);

    @Select("select *from pipelines where start_node_id = #{nodeId} or end_node_id = #{nodeId}")
    List<Pipelines> getPipelinesByNodeId(int nodeId);

    /**
     * SELECT node_id, latitude, longitude, created_at, updated_at
     * FROM nodes
     * WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(116.397455, 39.909187)) <= 2000;
     */
    /*@Select("select node_id, latitude, longitude, created_at, updated_at " +
            "from nodes " +
            "WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(#{longitude}, #{latitude})) <= #{radius}")
    List<Nodes> getNodesByRange(Double latitude, Double longitude, Double radius);*/
    @Select("SELECT node_id AS nodeId, latitude, longitude " +
            "FROM nodes " +
            "WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(#{longitude}, #{latitude})) <= #{radius} * 1000")
    List<Map<String, Object>> getNodesByRange(@Param("latitude") Double latitude,
                                              @Param("longitude") Double longitude,
                                              @Param("radius") Double radius);

// 查询最近的中心点
    @Select("SELECT center_id, center_latitude, center_longitude, region_radius " +
            "FROM region_centers " +
            "ORDER BY ST_Distance_Sphere(POINT(center_longitude, center_latitude), POINT(#{longitude}, #{latitude})) ASC " +
            "LIMIT 1")
    RegionCenters findNearestCenter(BigDecimal latitude, BigDecimal longitude);
}
