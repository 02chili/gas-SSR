package com.chili.service;

public interface IncidentVerificationService {

    void dispatch(Long alarmTicketId, Integer userId);
}
