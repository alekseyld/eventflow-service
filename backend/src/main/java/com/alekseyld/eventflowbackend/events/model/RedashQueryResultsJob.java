package com.alekseyld.eventflowbackend.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RedashQueryResultsJob(
        int status,
        String error,
        String id,
        @JsonProperty(value = "query_result_id")
        String queryResultId
) {

        public final static int STATUS_ERROR = 4;
//        public final static int STATUS_OK =  3;
}
