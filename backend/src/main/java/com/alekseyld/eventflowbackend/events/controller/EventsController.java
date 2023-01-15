package com.alekseyld.eventflowbackend.events.controller;

import com.alekseyld.eventflowbackend.events.domain.service.EventsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventsController {

    private EventsService eventsService;

    @GetMapping("/latest")
    public String getLatestEvents(
            @RequestParam("last_fetch_time")
            long lastFetchTime,
            @RequestParam("client_id")
            String clientId
    ) {
        return eventsService.getLatestEvents(lastFetchTime, clientId);
    }
}
