package org.jim.hystrix.schedule;

import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.metric.consumer.HystrixDashboardStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Observer;

import java.util.Collection;

@Slf4j
@Component
public class RequestStats {

    private boolean processing = false;

    @Scheduled(fixedDelay = 1000L * 20)
    public void runTask() {
        log.info("Run task ...");
        if (!processing) {
            process2();
        }
        log.info("... After task");
    }

    public void process2() {
        log.info("Enter process");
        Collection<HystrixCommandMetrics> metricsCollection = HystrixCommandMetrics.getInstances();
        for (HystrixCommandMetrics metrics : metricsCollection) {
            String command = metrics.getCommandKey().name();
            int timeMean = metrics.getExecutionTimeMean();
            int time90 = metrics.getExecutionTimePercentile(0.9);
            long success = metrics.getCumulativeCount(HystrixEventType.SUCCESS);
            log.info("Command metrics: {}, {}, {}, {}", command, timeMean, time90, success);
        }
        log.info("Exit process");
    }

    public void process() {
        log.info("Enter process");
        processing = true;
        Observable<HystrixDashboardStream.DashboardData> observable = HystrixDashboardStream.getInstance().observe();
        observable.subscribe(new Observer<HystrixDashboardStream.DashboardData>() {
            @Override
            public void onCompleted() {
               log.info("onCompleted");
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("onError", throwable);
            }

            @Override
            public void onNext(HystrixDashboardStream.DashboardData dashboardData) {
                if (dashboardData.getCommandMetrics().isEmpty()) {
                    return;
                }
                log.info("onNext: {}", dashboardData.getCommandMetrics());
            }
        });
        processing = false;
        log.info("Exit process");
    }
}
