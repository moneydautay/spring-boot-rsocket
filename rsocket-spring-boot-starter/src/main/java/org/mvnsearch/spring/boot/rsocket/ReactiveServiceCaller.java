package org.mvnsearch.spring.boot.rsocket;

/**
 * reactive service caller
 *
 * @author linux_china
 */
public interface ReactiveServiceCaller {
    /**
     * invoke real service
     *
     * @param serviceName service full name
     * @param rpc         rpc name
     * @param args        args
     * @return result
     */
    Object invoke(String serviceName, String rpc, Object[] args) throws Exception;
}
