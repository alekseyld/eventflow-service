package com.alekseyld.eventflowbackend.events.model;

import com.alekseyld.eventflowbackend.events.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;

public record RedashQueryResultsDataRow(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATE_FORMAT)
        Date date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATETIME_FORMAT)
        Date datetime,
        @NonNull
        String event,
        @Nullable
        String token,
        @JsonProperty("visit_id")
        String visitId,
        String agent,
        String tag,
        String params
) {
}
