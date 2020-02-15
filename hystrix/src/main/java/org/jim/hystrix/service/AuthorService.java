package org.jim.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import lombok.extern.slf4j.Slf4j;
import org.jim.hystrix.exception.BookException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.Future;

@Slf4j
@Service
@DefaultProperties(commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
})
public class AuthorService {

    @HystrixCommand(fallbackMethod = "fallback")
    public Future<String> author(String id, DeferredResult<String> output) {
        log.info("Start ......");
        Future<String> future = new AsyncResult<String>() {
            @Override
            public String invoke() {
                log.info("Begin invoke .....");

                for (int i = 0; i < 2_000_000; i++) {
                    UUID.randomUUID();
                }
                String value = "Hello";
                if (!output.isSetOrExpired()) {
                    output.setResult(value);
                } else {
                    log.error("Already sent");
                }

                log.info("End invoke ....");
                return value;
            }
        };
        log.info("..........End");
        return future;
    }

    public String fallback(String category, DeferredResult<String> output, Throwable e) {
        log.error("Fallback", e);
        output.setErrorResult("Error");
        return "Error";
    }

}
