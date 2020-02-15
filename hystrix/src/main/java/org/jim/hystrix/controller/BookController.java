package org.jim.hystrix.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.jim.hystrix.service.AuthorService;
import org.jim.hystrix.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 一个简单的controller
 *
 * @author Jim
 */
@Slf4j
@RestController("BookCtl")
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

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

    @RequestMapping(value = "/book")
    public DeferredResult<String> book() {
        log.info("Enter book ....");
        DeferredResult<String> output = new DeferredResult<>(2000L);
        bookService.book("test", output);
        log.info("Exit book ....");
        return output;
    }

    @RequestMapping(value = "/author")
    public DeferredResult<String> author() {
        log.info("Enter author ....");
        DeferredResult<String> output = new DeferredResult<>(2000L);
        authorService.author("test", output);
        log.info("Exit author ....");
        return output;
    }
}
