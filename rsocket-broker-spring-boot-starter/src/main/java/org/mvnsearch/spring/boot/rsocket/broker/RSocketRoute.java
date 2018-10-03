package org.mvnsearch.spring.boot.rsocket.broker;

import java.util.List;

/**
 * RSocket Route
 *
 * @author linux_china
 */
public class RSocketRoute {
    /**
     * endpoint, such as service in k8s or app name
     */
    private String endpoint;
    /**
     * service list
     */
    private List<String> services;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
