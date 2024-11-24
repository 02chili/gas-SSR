package com.chili.mapper;


import com.chili.entity.AlarmTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface AlarmTicketMapper {
    @Insert("insert into alarm_ticket (event_type, occurrence_time, pipeline_location, pipeline_material, pipeline_depth, pipeline_diameter, " +
            "pipeline_pressure, population_density, casualties, nearby_area, leak_rate, airflow_direction, response_level) VALUES " +
            "(#{eventType},#{occurrenceTime},#{pipelineLocation},#{pipelineMaterial},#{pipelineDepth},#{pipelineDiameter}" +
            ",#{pipelinePressure},#{populationDensity},#{casualties},#{nearbyArea}," +
            "#{leakRate},#{airflowDirection},#{responseLevel})")
    void save(AlarmTicket alarmTicket);

    @Select("select *from alarm_ticket limit #{begin},#{end}")
    List<AlarmTicket> page(int begin, int end);

    @Select("select * from alarm_ticket where id = #{id}")
    AlarmTicket getById(Long id);
}
