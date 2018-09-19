package org.mvnsearch.spring.boot.rsocket;

import java.io.Serializable;
import java.util.Map;

/**
 * rsocket invocation metadata
 *
 * @author linux_china
 */
public class InvocationMetaData implements Serializable {
    private String service;
    private String rpc;
    private Map<String, String> attributes;

    public InvocationMetaData() {
    }

    public InvocationMetaData(String service, String rpc) {
        this.service = service;
        this.rpc = rpc;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
