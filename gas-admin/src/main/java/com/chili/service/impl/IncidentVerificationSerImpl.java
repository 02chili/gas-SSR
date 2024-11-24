package com.chili.service.impl;

import com.chili.client.ReportClient;
import com.chili.entity.AlarmTicket;
import com.chili.exception.BaseException;
import com.chili.result.Result;
import com.chili.service.IncidentVerificationService;
import com.alibaba.fastjson.JSON;
import com.chili.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IncidentVerificationSerImpl implements IncidentVerificationService {

    private final WebSocketServer webSocketServer;
    private final ReportClient reportClient;

    private static final Logger logger = LoggerFactory.getLogger(IncidentVerificationSerImpl.class);

    @Override
    public void dispatch(Long alarmTicketId, Integer userId) {
        logger.info("派遣报警但为 ID: {} 给用户 {}", alarmTicketId, userId);

        // 获取报警单信息
        Result<AlarmTicket> result = reportClient.getById(alarmTicketId);
        if (result == null || result.getData() == null) {
            logger.error("未找到报警单 ID: {}", alarmTicketId);
            throw new BaseException("未找到报警单信息为 " + alarmTicketId +"的报警单");
        }

        // 封装进map对象，只封装报警单的id，抢险人员在收到信息之后再调用一次根据id查询报警单的详细信息
        Map<String, Long> map = new HashMap<>();
        map.put("alarmTicketId", alarmTicketId);
        String json = JSON.toJSONString(map);

        // 发送消息给指定用户
        webSocketServer.sendMessageToUser(userId, json);
        logger.info("Message sent to user ID: {}", userId);
    }
}
