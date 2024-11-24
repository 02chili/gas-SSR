package com.chili.controller;

import com.chili.dto.AlarmTicketDTO;
import com.chili.dto.AlarmTicketPageQueryDTO;
import com.chili.entity.AlarmTicket;
import com.chili.result.PageResult;
import com.chili.result.Result;
import com.chili.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Slf4j
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/report")
    public Result report(@RequestBody AlarmTicketDTO alarmTicketDTO) {
        AlarmTicket alarmTicket = new AlarmTicket();
        BeanUtils.copyProperties(alarmTicketDTO, alarmTicket);
        reportService.save(alarmTicket);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> Page(AlarmTicketPageQueryDTO alarmTicketPageQueryDTO){
        PageResult pageResult = reportService.page(alarmTicketPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/getById/{id}")
    public Result<AlarmTicket> getById(@PathVariable("id") Long id) {
        log.info("查询id为{}的用户信息", id);
        AlarmTicket alarmTicket = reportService.getById(id);
        return Result.success(alarmTicket);
    }

}
