package tw.howie.load.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author howie
 * @since 2015/4/18
 */
@RestController
@RequestMapping("/api")
public class DummyController {

    private static final Logger logger = LoggerFactory.getLogger(DummyController.class);

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

}
