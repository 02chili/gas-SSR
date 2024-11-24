package com.chili.service.impl;

import com.alibaba.fastjson.JSON;
import com.chili.dto.AlarmTicketPageQueryDTO;
import com.chili.entity.AlarmTicket;
import com.chili.mapper.AlarmTicketMapper;
import com.chili.result.PageResult;
import com.chili.service.ReportService;
import com.chili.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final AlarmTicketMapper warningMapper;
    private final WebSocketServer webSocketServer;

    @Override
    public void save(AlarmTicket alarmTicket) {
        alarmTicket.setOccurrenceTime(LocalDateTime.now());
        Map map = new HashMap();
        map.put("pipelineId", alarmTicket.getPipelineId());
        String jsonString = JSON.toJSONString(map);
        //将警报信息通知给全体管理员
        webSocketServer.sendToAllAdmins(jsonString);
        //将报警单信息储存到数据库中
        warningMapper.save(alarmTicket);
    }

    @Override
    public PageResult page(AlarmTicketPageQueryDTO alarmTicketPageQueryDTO) {
        int begin = (alarmTicketPageQueryDTO.getPage()-1)*alarmTicketPageQueryDTO.getPagesize();
        int end = begin+alarmTicketPageQueryDTO.getPagesize();
        List<AlarmTicket>list = warningMapper.page(begin,end);
        long total = list.size();
        return new PageResult(total, list);
    }

    @Override
    public AlarmTicket getById(Long id) {
        AlarmTicket alarmTicket = warningMapper.getById(id);
        return alarmTicket;
    }
}
