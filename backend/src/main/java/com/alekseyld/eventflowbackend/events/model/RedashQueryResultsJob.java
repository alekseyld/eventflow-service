package com.alekseyld.eventflowbackend.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RedashQueryResultsJob(
        String id,

        @JsonProperty(value = "query_result_id")
        String queryResultId
) {
}
