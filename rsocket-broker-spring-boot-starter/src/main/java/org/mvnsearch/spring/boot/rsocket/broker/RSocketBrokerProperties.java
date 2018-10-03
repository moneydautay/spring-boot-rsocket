package org.mvnsearch.spring.boot.rsocket.broker;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * broker configuration
 *
 * @author linux_china
 */
@ConfigurationProperties(
        prefix = "rsocket.broker"
)
public class RSocketBrokerProperties {
    /**
     * route information
     */
    private List<RSocketRoute> routes;

    public List<RSocketRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RSocketRoute> routes) {
        this.routes = routes;
    }
}
