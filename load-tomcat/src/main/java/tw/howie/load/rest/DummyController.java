package tw.howie.load.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tw.howie.load.DummyRequest;
import tw.howie.load.DummyResponse;
import tw.howie.load.RequestClient;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author howie
 * @since 2015/4/18
 */
@RestController
@RequestMapping("/api")
public class DummyController {

    private static final Logger logger = LoggerFactory.getLogger(DummyController.class);

    @Inject
    private RequestClient client;

    @RequestMapping(value = "/echo/{input}",
                    method = RequestMethod.GET)
    public String echo(@PathVariable String input) {
        return input;
    }

    @RequestMapping(value = "/watchcat",
                    method = RequestMethod.GET)
    public String watchcat() {

        //FIXME
        return "service_ok";
        //        if (jdbcTemplate.queryForObject("select 1", Integer.class)
        //                        .equals(1)) {
        //            return "service_ok";
        //        }
        //        return "failed";
    }

    @RequestMapping(value = "/async/{input}",
                    method = RequestMethod.GET)
    public String asyncJob(@PathVariable String input) {

        try {

            DummyResponse respones = client.send(new DummyRequest(input))
                                           .get(400L, TimeUnit.MILLISECONDS);

            return respones.getResponse();

        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            logger.warn("InterruptedException: {}", e.getMessage());
            return "InterruptedException";
        } catch (ExecutionException e) {
            logger.warn("request error: {}", e.getMessage());
            return "error";
        } catch (TimeoutException e) {
            logger.warn("request timeout!");
            return "time out! more than 400 ms";
        }

    }

}
