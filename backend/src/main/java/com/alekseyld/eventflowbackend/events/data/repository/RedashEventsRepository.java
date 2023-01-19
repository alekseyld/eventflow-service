package com.alekseyld.eventflowbackend.events.data.repository;

import com.alekseyld.eventflowbackend.events.DateUtils;
import com.alekseyld.eventflowbackend.events.configuration.properties.RedashConfigurationProperties;
import com.alekseyld.eventflowbackend.events.model.RedashQueryResultsDataRow;
import com.alekseyld.eventflowbackend.events.model.RedashQueryResultsRequest;
import com.alekseyld.eventflowbackend.events.model.RedashQueryResultsResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.alekseyld.eventflowbackend.events.model.RedashQueryResultsJob.STATUS_ERROR;

@Repository
@Log4j2
public class RedashEventsRepository {

    private final RedashConfigurationProperties properties;

    private final RestTemplate restTemplate;

    public RedashEventsRepository(
            RedashConfigurationProperties properties,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.properties = properties;
        restTemplate = restTemplateBuilder.build();
    }

    @NonNull
    public List<RedashQueryResultsDataRow> getLatestEvents(
            long lastFetchTime,
            String clientId
    ) {
        var response = fetchEvents(lastFetchTime, clientId);

        if (response != null) {
            if (response.queryResult() != null) {
                return response.queryResult().data().rows();
            }

            if (response.job() != null) {
                var id = response.job().id();
                var queryResultId = listenForQueryResultId(id);

                if (queryResultId != null) {

                    var queryResultResponse = fetchEventsById(queryResultId);

                    if (queryResultResponse != null && queryResultResponse.queryResult() != null) {
                        return queryResultResponse.queryResult().data().rows();
                    }

                } else {
                    log.debug("Cannot get queryResults, return empty");
                }
            }
        }

        return Collections.emptyList();
    }

    @Nullable
    private RedashQueryResultsResponse fetchEventsById(
            @NonNull String queryResultId
    ) {

        var requestGetJob = new HttpEntity<>(getDefaultHeaders());

        var queryResult = restTemplate.exchange(
                properties.getEventFetchUrl() + "/" + queryResultId,
                HttpMethod.GET,
                requestGetJob,
                RedashQueryResultsResponse.class
        );

        return queryResult.getBody();
    }

    @Nullable
    private String listenForQueryResultId(@NonNull String jobId) {
        log.debug(String.format("Get job id = %s", jobId));

        var requestGetJob = new HttpEntity<>(getDefaultHeaders());
        String queryResultId = null;
        int i = 0;

        while (queryResultId == null && i < 20) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;

            log.debug(String.format("Wait for queryResults (%d)", i));

            var jobResult = restTemplate.exchange(
                    properties.getJobGetUrl() + "/" + jobId,
                    HttpMethod.GET,
                    requestGetJob,
                    RedashQueryResultsResponse.class
            );

            var body = jobResult.getBody();
            if (body != null && body.job() != null) {
                if (body.job().status() == STATUS_ERROR) {
                    break;
                }

                queryResultId = body.job().queryResultId();
                log.debug(String.format("queryResultId = %s", queryResultId));
            }
        }

        return queryResultId;
    }

    @Nullable
    private RedashQueryResultsResponse fetchEvents(
            long lastFetchTime,
            @NonNull String clientId
    ) {
        var end = new Date();
        Date start = getStartDate(end, lastFetchTime);

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

        var headers = getDefaultHeaders();

        var request = new HttpEntity<>(body, headers);

        var response = restTemplate.exchange(
                properties.getEventFetchUrl(),
                HttpMethod.POST,
                request,
                RedashQueryResultsResponse.class
        );

        return response.getBody();
    }

    @NonNull
    private HttpHeaders getDefaultHeaders() {
        var headers = new HttpHeaders();
        headers.setBasicAuth(properties.getBasicAuthToken());
        headers.add("Cookie", String.format("session=%s", properties.getSessionCookie()));
        return headers;
    }

    @NonNull
    private Date getStartDate(Date endDate, long lastFetchTime) {
        if (lastFetchTime == 0) {
            return new Date(endDate.getTime() - 5 * 60 * 1000);
        } else {
            return new Date(endDate.getTime() - lastFetchTime);
        }
    }
}
