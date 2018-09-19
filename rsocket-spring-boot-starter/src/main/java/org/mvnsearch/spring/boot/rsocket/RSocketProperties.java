package org.mvnsearch.spring.boot.rsocket;

import org.springframework.boot.context.properties.ConfigurationProperties;

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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
