package com.alekseyld.eventflowbackend.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

public record RedashQueryResultsResponse(
        @Nullable
        @JsonProperty(value = "query_result")
        RedashQueryResults queryResult,

        @Nullable
        RedashQueryResultsJob job
) {
}