package com.alekseyld.eventflowbackend.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Запрос на создание джобы
 */
@Builder
@Getter
public class RedashQueryResultsRequest {

    @JsonProperty(value = "data_source_id")
    private final int dataSourceId;

    @NonNull
    @JsonProperty(value = "query")
    private final String query;
}
