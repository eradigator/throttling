package com.ecom.throttling.service;

import org.springframework.stereotype.Service;

@Service
public class SomeServiceImpl implements SomeService {

    @IpThrottling
    public void someMethod() {
    }
}
