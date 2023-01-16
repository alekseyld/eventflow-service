package com.alekseyld.eventflowbackend.events.model;

import java.util.List;

public record LatestEventsResponse(
        List<RedashQueryResultsDataRow> events,
        String clientId
) {
}
