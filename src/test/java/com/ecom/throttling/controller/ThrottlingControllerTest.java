package com.ecom.throttling.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ThrottlingControllerTest {

    private static final String X_FORWARDED_HEADER = "X-Forwarded-For";

    @Autowired
    private MockMvc mockMvc;

    @Value("${throttling.count.limit}")
    private int requestCountLimit;

    @Value("${throttling.time.limit}")
    private long timeLimit;


    @Test
    void testGet() throws Exception {
        RequestBuilder rb1 = get("/v1/throttling").header(X_FORWARDED_HEADER, "10.0.0.1");
        RequestBuilder rb2 = get("/v1/throttling").header(X_FORWARDED_HEADER, "10.0.0.2");

        Thread t1 = new Thread(() -> {
            for (int i=0; i<requestCountLimit; i++) {
                try {
                    mockMvc.perform(rb1).andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i=0; i<requestCountLimit; i++) {
                try {
                    mockMvc.perform(rb2).andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        mockMvc.perform(rb1).andExpect(status().is(HttpStatus.BAD_GATEWAY.value()));
        mockMvc.perform(rb2).andExpect(status().is(HttpStatus.BAD_GATEWAY.value()));

        Thread.sleep(timeLimit);

        mockMvc.perform(rb1).andExpect(status().isOk());
        mockMvc.perform(rb2).andExpect(status().isOk());
    }
}