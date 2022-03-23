package com.ecom.throttling.service;

import com.ecom.throttling.exception.ThrottlingException;
import com.ecom.throttling.util.RequestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class ThrottlingServiceImpl implements ThrottlingService {

    @Value("${throttling.count.limit}")
    private int requestCountLimit;

    @Value("${throttling.time.limit}")
    private long timeLimit;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, List<Long>> map = new HashMap<>();

    @Override
    public void throttle() {
        String ip = RequestUtil.getIp();
        long cur = System.currentTimeMillis();

        List<Long> l = map.get(ip);
        if (l != null && l.size() > requestCountLimit - 1) {
            l = l.stream()
                    .filter(e -> cur - e < timeLimit)
                    .collect(Collectors.toList());
            if (l.size() > requestCountLimit - 1) throw new ThrottlingException();
        }

        try {
            lock.writeLock().lock();

            if (l == null) {
                map.put(ip, new ArrayList<>(List.of(cur)));
            } else {
                l.add(cur);
                map.put(ip, l);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

}

