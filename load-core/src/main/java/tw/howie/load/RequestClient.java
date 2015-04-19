package tw.howie.load;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author howie
 * @since 2015/4/19
 */
@Component
public class RequestClient {
    private static final Logger logger = LoggerFactory.getLogger(RequestClient.class);

    @Value("${time:500}")
    private String executeTime;

    private ExecutorService requestClientExecutor;

    public RequestClient() {

    }

    @PreDestroy
    public void destroy() throws Exception {

        logger.info("begin shutdown...");
        // shut down dsp clients first, so no further auction proceed
        requestClientExecutor.shutdownNow();

        logger.info("shutdowned");
    }

    @PostConstruct
    public void init() {

        requestClientExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(
                "request-client-%d")
                                                                                        .build());

    }

    private long randomTaskExecuteTime() {
        //DO something slow~
        long leftLimit = 100L;
        long rightLimit = Long.valueOf(executeTime);
        return leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

    }

    public CompletableFuture<DummyResponse> send(DummyRequest request) {

        return CompletableFuture.supplyAsync(() -> {

            long generatedLong = randomTaskExecuteTime();
            try {

                Thread.sleep(generatedLong);

            } catch (InterruptedException e) {
                Thread.interrupted();
            }

            return new DummyResponse(request.getRequest() + " take " + generatedLong + " ms.");

        }, requestClientExecutor);

    }

}
