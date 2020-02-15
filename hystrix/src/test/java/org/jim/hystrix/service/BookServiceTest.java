package org.jim.hystrix.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    public void testPopular() {
        log.info("test .......");
        String category = null;
        try {
            category = "1";
            String result = bookService.popularBooks(category);
            log.info("test result: {}", result);
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getClass().getName());
            e.printStackTrace();
        }
        log.info("....... test");
    }

    @Test
    public void testName() throws Exception {
        log.info("test ....");
        String id = "124";
        bookService.bookName(id);

        log.info("sleep");
        Thread.sleep(2000L);
        log.info(".... test");
    }

    @Test
    public void testAuthor() throws Exception {
        log.info("test .......");
        String book = "ABC";

        // 为什么是同一个线程
        Observable<String> observable = bookService.bookAuthors(book);

        Executors.newFixedThreadPool(5).submit(() -> {
            observable.subscribe(new Observer<String>() {
                @Override
                public void onCompleted() {
                    log.info("OnCompleted: {}", System.currentTimeMillis());
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("OnError: {}", System.currentTimeMillis(), throwable);
                }

                @Override
                public void onNext(String s) {
                    log.info("OnNext: {}, {}", System.currentTimeMillis(), s);
                }
            });
        });

        log.info("Begin slepp: {}", System.currentTimeMillis());
        Thread.sleep(10000L);
        log.info("Sleep: {}", System.currentTimeMillis());

        log.info("....... test");
    }
}
