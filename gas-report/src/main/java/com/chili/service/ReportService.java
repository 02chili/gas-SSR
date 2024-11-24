package com.chili.service;

import com.chili.dto.AlarmTicketPageQueryDTO;
import com.chili.entity.AlarmTicket;
import com.chili.result.PageResult;

public interface ReportService {
    void save(AlarmTicket alarmTicket);

    PageResult page(AlarmTicketPageQueryDTO alarmTicketPageQueryDTO);

    AlarmTicket getById(Long id);
}
