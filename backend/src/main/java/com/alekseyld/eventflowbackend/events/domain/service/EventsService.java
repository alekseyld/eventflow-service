package com.alekseyld.eventflowbackend.events.domain.service;

import com.alekseyld.eventflowbackend.events.DateUtils;
import com.alekseyld.eventflowbackend.events.configuration.properties.RedashConfigurationProperties;
import com.alekseyld.eventflowbackend.events.model.LatestEventsResponse;
import com.alekseyld.eventflowbackend.events.model.RedashQueryResultsRequest;
import com.alekseyld.eventflowbackend.events.model.RedashQueryResultsResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class EventsService {

    private final RestTemplate restTemplate;

    private final RedashConfigurationProperties properties;

    EventsService(
            RestTemplateBuilder restTemplateBuilder,
            RedashConfigurationProperties properties
    ) {
        this.properties = properties;
        restTemplate = restTemplateBuilder.build();
    }

    public LatestEventsResponse getLatestEvents(
            long lastFetchTime,
            String clientId
    ) {

        var end = new Date();
        Date start;

        if (lastFetchTime == 0) {
            start = new Date(end.getTime() - 5 * 60 * 1000);
        } else {
            start = new Date(end.getTime() - lastFetchTime);
        }

        var query = String.format(
                properties.getEventSqlQuery(),
                properties.getEventSqlTable(),
                DateUtils.defaultDateFormatter.format(start),
                DateUtils.defaultDateFormatter.format(end),
                DateUtils.defaultDatetimeFormatter.format(start),
                DateUtils.defaultDatetimeFormatter.format(end),
                clientId
        );

        log.debug(query);

        var body = RedashQueryResultsRequest.builder()
                .dataSourceId(2)
                .query(query)
                .build();

        var headers = new HttpHeaders();
        headers.setBasicAuth(properties.getBasicAuthToken());
        headers.add("Cookie", String.format("session=%s", properties.getSessionCookie()));

        var request = new HttpEntity<>(body, headers);

        try {
            var object = restTemplate.exchange(
                    properties.getEventFetchUrl(),
                    HttpMethod.POST,
                    request,
                    RedashQueryResultsResponse.class
            );

            if (object.getBody() != null && object.getBody().queryResult() != null) {
                log.debug("Return queryResults after first request");

                return mapFromResponse(object.getBody(), clientId);
            }

            var id = Objects.requireNonNull(object.getBody().job()).id();

            log.debug(String.format("Get job id = %s", id));

            var requestGetJob = new HttpEntity<>(headers);
            String queryResultId = null;
            int i = 0;

            while (queryResultId == null && i < 20) {
                TimeUnit.MILLISECONDS.sleep(500);
                i++;

                log.debug(String.format("Wait for queryResults (%d)", i));

                var jobResult = restTemplate.exchange(
                        properties.getJobGetUrl() + "/" + id,
                        HttpMethod.GET,
                        requestGetJob,
                        RedashQueryResultsResponse.class
                );

                if (jobResult.getBody() != null && jobResult.getBody().job() != null) {
                    queryResultId = jobResult.getBody().job().queryResultId();
                    log.debug(String.format("queryResultId = %s", queryResultId));
                }
            }

            if (queryResultId == null) {
                log.debug("Cannot get queryResults, return empty");
                return mapFromResponse(null, clientId);
            }

            var queryResult = restTemplate.exchange(
                    properties.getEventFetchUrl() + "/" + queryResultId,
                    HttpMethod.GET,
                    requestGetJob,
                    RedashQueryResultsResponse.class
            );

            return mapFromResponse(queryResult.getBody(), clientId);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private LatestEventsResponse mapFromResponse(
            @Nullable RedashQueryResultsResponse response,
            String clientId
    ) {
        if (response == null || response.queryResult() == null) {
            return new LatestEventsResponse(List.of(), clientId);
        }

        return new LatestEventsResponse(response.queryResult().data().rows(), clientId);
    }
}
