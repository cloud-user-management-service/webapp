package com.myweb.webapp.service.impl;

import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.myweb.webapp.config.StatsdConfig;
import com.myweb.webapp.service.MetricsService;
import org.springframework.stereotype.Service;

@Service
public class MetricsServiceImpl implements MetricsService {
    // private static StatsDClient statsDClient = new NonBlockingStatsDClient("my.prefix", "statsd-host", 8125);
    private StatsDClient statsDClient;

    public MetricsServiceImpl(StatsDClient statsDClient) {
        this.statsDClient = statsDClient;
    }
    // Add a new method to record the duration of an API call and increment a counter
    public void recordApiCall(String apiName, long duration) {
        statsDClient.incrementCounter("api.calls." + apiName);
        statsDClient.recordExecutionTime("api.response_time." + apiName, duration);
    }

    // Add a new method to record the duration of a database query
    public void recordDatabaseQuery(String queryName, long duration) {
        statsDClient.recordExecutionTime("db.query_time." + queryName, duration);
    }

    // Add a new method to record the duration of an S3 call
    public void recordS3Call(String operationName, long duration) {
        statsDClient.recordExecutionTime("s3.call_time." + operationName, duration);
    }
}

