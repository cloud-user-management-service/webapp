package com.myweb.webapp.service;

public interface MetricsService {
    void recordApiCall(String metricName, long time);
    void recordDatabaseQuery(String metricName, long time);
    void recordS3Call(String metricName, long time);
    
}
