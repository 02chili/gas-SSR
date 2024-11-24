package com.chili.client;

import com.chili.entity.AlarmTicket;
import com.chili.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gas-report")
public interface ReportClient {
    @GetMapping("/report/getById/{id}")
    public Result<AlarmTicket> getById(@PathVariable("id") Long id);
}
