package org.mvnsearch.spring.boot.rsocket;

import java.util.Set;

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
    Object invoke(String serviceName, String rpc, Object... args) throws Exception;

    /**
     * validate service
     *
     * @param serviceName service
     * @param rpc         rpc
     * @return exist mark
     */
    boolean contains(String serviceName, String rpc);

    /**
     * find all service
     *
     * @return service list
     */
    Set<String> findAllServices();
}
