package org.mvnsearch.spring.boot.rsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RSocket requester auto configuration
 *
 * @author linux_china
 */
@Configuration
@EnableConfigurationProperties(RSocketProperties.class)
public class RSocketRequesterAutoConfiguration {
    private Logger log = LoggerFactory.getLogger(RSocketRequesterAutoConfiguration.class);
    @Autowired
    RSocketProperties properties;

    //todo create Mono<RSocket>
}
