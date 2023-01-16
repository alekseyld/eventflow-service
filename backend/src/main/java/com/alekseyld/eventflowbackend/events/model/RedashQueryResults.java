package com.alekseyld.eventflowbackend.events.model;

import java.util.List;

public record RedashQueryResults(Data data) {

    public record Data(
            List<RedashQueryResultsDataRow> rows
    ) {
    }
}
