package org.mvnsearch.spring.boot.rsocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * RSocketService annotation processor
 *
 * @author linux_china
 */
public class RSocketServiceAnnotationProcessor implements BeanPostProcessor, ReactiveServiceCaller {
    public Map<String, Object> rsocketServices = new HashMap<>();
    public Map<String, Method> methodInvokeEntrances = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        scanRSocketServiceAnnotation(bean, beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    protected void scanRSocketServiceAnnotation(Object bean, String beanName) {
        Class<?> managedBeanClass = bean.getClass();
        RSocketService reactiveService = AnnotationUtils.findAnnotation(managedBeanClass, RSocketService.class);
        if (reactiveService != null) {
            registerRSocketService(reactiveService, bean);
        }
    }

    private void registerRSocketService(RSocketService reactiveService, Object bean) {
        String classFullName = reactiveService.serviceInterface().getCanonicalName();
        rsocketServices.put(classFullName, bean);
        for (Method method : reactiveService.serviceInterface().getMethods()) {
            methodInvokeEntrances.put(classFullName + "." + method.getName(), method);
        }
    }

    @Override
    public Object invoke(String serviceName, String rpc, Object... args) throws Exception {
        Method method = methodInvokeEntrances.get(serviceName + "." + rpc);
        return method.invoke(rsocketServices.get(serviceName), args);
    }

    @Override
    public boolean contains(String serviceName, String rpc) {
        return methodInvokeEntrances.containsKey(serviceName + "." + rpc);
    }

    @Override
    public Set<String> findAllServices() {
        return rsocketServices.keySet();
    }
}
