package com.alekseyld.eventflowbackend.events.domain.service;

import com.alekseyld.eventflowbackend.events.data.repository.RedashEventsRepository;
import com.alekseyld.eventflowbackend.events.model.LatestEventsResponse;
import com.alekseyld.eventflowbackend.events.model.RedashQueryResultsDataRow;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class EventsService {

    private final RedashEventsRepository repository;

    public LatestEventsResponse getLatestEvents(
            long lastFetchTime,
            String clientId
    ) {
        return mapFromResponse(
                repository.getLatestEvents(lastFetchTime, clientId),
                clientId
        );
    }

    private LatestEventsResponse mapFromResponse(
            List<RedashQueryResultsDataRow> rows,
            String clientId
    ) {
        return new LatestEventsResponse(rows, clientId);
    }
}
