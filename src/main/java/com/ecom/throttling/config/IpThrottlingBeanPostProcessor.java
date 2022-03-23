package com.ecom.throttling.config;

import com.ecom.throttling.service.IpThrottling;
import com.ecom.throttling.service.ThrottlingService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class IpThrottlingBeanPostProcessor implements BeanPostProcessor {

    private final Map<String, Class<?>> ipThrottlingAnnotatedBeans = new HashMap<>();

    private final ThrottlingService throttlingService;

    public IpThrottlingBeanPostProcessor(ThrottlingService throttlingService) {
        this.throttlingService = throttlingService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        boolean isIpThrottlingAnnotationPresent = Arrays.stream(methods)
                .anyMatch(m -> m.isAnnotationPresent(IpThrottling.class));
        if (isIpThrottlingAnnotationPresent) {
            ipThrottlingAnnotatedBeans.put(beanName, bean.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = ipThrottlingAnnotatedBeans.get(beanName);
        if (beanClass == null) return bean;

        return Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                beanClass.getInterfaces(),
                (proxy, method, args) -> {
                    throttlingService.throttle();
                    return method.invoke(bean, args);
                });
    }
}
