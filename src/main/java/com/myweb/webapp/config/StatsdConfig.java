package com.myweb.webapp.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class StatsdConfig {

    @Bean
    public StatsDClient statsDClient() {
        return new NonBlockingStatsDClient("webapp.api", "localhost", 8125);
    }
}

// public class MetricsConfig {
//     public static final StatsDClient statsd = new NonBlockingStatsDClient(
//         "myapp",                 // Prefix for metrics
//         "localhost",             // StatsD server hostname
//         8125                     // StatsD port
//     );

// }
