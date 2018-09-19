package org.mvnsearch.spring.boot.rsocket;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * rsocket Service annotation
 *
 * @author linux_china
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
public @interface RSocketService {
    /**
     * service interface
     *
     * @return service interface
     */
    Class serviceInterface();
}
