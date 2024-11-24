package com.chili.controller;

import com.chili.result.Result;
import com.chili.service.IncidentVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class IncidentVerificationController {
    private final IncidentVerificationService incidentVerificationService;

    @PutMapping("/dispatch/{alarmTicketId}/{userId}")
    public Result dispatch(@PathVariable Long alarmTicketId, @PathVariable Integer userId) {
        // 执行派单逻辑
        incidentVerificationService.dispatch(alarmTicketId, userId);

        return Result.success();
    }

}
