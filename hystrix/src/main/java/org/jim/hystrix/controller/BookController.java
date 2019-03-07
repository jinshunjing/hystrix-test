package org.jim.hystrix.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一个简单的controller
 *
 * @author Jim
 */
@RestController("BookCtl")
@RequestMapping("/book")
public class BookController {

    /**
     * 配置断路器，接口超时就调用fallback方法
     *
     * @return
     * @throws InterruptedException
     */
    @RequestMapping(value = "/version")
    @HystrixCommand(fallbackMethod = "fallback_version", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })
    public String version() throws InterruptedException {
        // 模拟阻塞的服务
        Thread.sleep(3_000L);
        return "Book v1.0.0";
    }

    private String fallback_version() {
        return "Request failed";
    }

}
