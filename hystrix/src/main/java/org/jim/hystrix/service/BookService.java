package org.jim.hystrix.service;

import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.contrib.javanica.annotation.*;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.jim.hystrix.exception.BookException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.plugins.RxJavaHooks;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;

@Slf4j
@Service
@DefaultProperties
public class BookService {

    @HystrixCommand(fallbackMethod = "fallback",
            ignoreExceptions = {BookException.class},
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
            })
    public Future<String> book(String id, DeferredResult<String> output) {
        log.info("Start ......");
        Future<String> future = new AsyncResult<String>() {
            @Override
            public String invoke() {
                log.info("Begin invoke .....");

                for (int i = 0; i < 2_000_000; i++) {
                    UUID.randomUUID();
                }
                String value = "Hello";
                output.setResult(value);

                log.info("End invoke ....");
                return value;
            }
        };
        log.info("..........End");
        return future;
    }

    public String fallback(String category, DeferredResult<String> output, Throwable e) {
        log.error("Fallback", e);
        return "Error";
    }


    @HystrixCommand(fallbackMethod = "fallback",
            ignoreExceptions = {BookException.class},
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
            })
    public String popularBooks(String category) throws BookException {
        log.info("Start..........");
        if (Objects.isNull(category)) {
            throw new BookException("Null category");
        }

        for (int i = 0; i < 10_000_000; i++) {
            UUID.randomUUID();
        }

        log.info("............End");
        return "Alpha";
    }

    public String fallback(String category, Throwable e) {
        log.error("Fallback", e);
        return "Error";
    }

    @HystrixCommand(fallbackMethod = "fallback",
            ignoreExceptions = {BookException.class},
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
            })
    public Future<String> bookName(String id) {
        log.info("Start ......");
        Future<String> future = new AsyncResult<String>() {
            @Override
            public String invoke() {
                log.info("Begin invoke .....");

                for (int i = 0; i < 1_000_000; i++) {
                    UUID.randomUUID();
                }

                log.info("End invoke ....");
                return null;
            }
        };
        log.info("..........End");
        return future;
    }

    @HystrixCommand(fallbackMethod = "fallback",
            observableExecutionMode = ObservableExecutionMode.LAZY,
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "20000")
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "10")
            })
    public Observable<String> bookAuthors(String book) {
        log.info("Enter book author ...");
        Observable<String> observable = Observable.unsafeCreate(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                log.info("Enter call: {}", System.currentTimeMillis());
                try {
                    if (!subscriber.isUnsubscribed()) {
                        log.info("Start business: {}", System.currentTimeMillis());
                        String author = "Ken";
                        for (int i = 0; i < 10_000_000; i++) {
                            UUID.randomUUID();
                        }
                        log.info("End business: {}", System.currentTimeMillis());
                        subscriber.onNext(author);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                log.info("Exit call: {}", System.currentTimeMillis());
            }
        });
        log.info("Exit book author ...");
        return observable;
    }
}
