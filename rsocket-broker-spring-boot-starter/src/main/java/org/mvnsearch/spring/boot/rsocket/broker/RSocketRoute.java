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
     * uri list
     */
    private List<String> uris;
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

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
