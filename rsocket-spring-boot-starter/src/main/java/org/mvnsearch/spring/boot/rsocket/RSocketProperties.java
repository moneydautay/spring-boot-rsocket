package org.mvnsearch.spring.boot.rsocket;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * RSocket Properties
 *
 * @author linux_china
 */
@ConfigurationProperties(
        prefix = "rsocket"
)
public class RSocketProperties {
    /**
     * listen port, default is 42252
     */
    private Integer port = 42252;
    /**
     * broker url, such tcp://127.0.0.1:42252
     */
    private String broker;
    /**
     * endpoints: interface full name to endpoint url
     */
    private Map<String, List<String>> endpoints;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public Map<String, List<String>> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, List<String>> endpoints) {
        this.endpoints = endpoints;
    }
}
