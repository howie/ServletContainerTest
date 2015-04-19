package tw.howie.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@ComponentScan(basePackages = {"tw.howie"})
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.ManagementSecurityAutoConfiguration.class})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Inject
    private Environment env;

    /**
     * Initializes Rmax_admin.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     */
    @PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            log.warn("No Spring profile configured, running with default configuration");
        }
        else {
            log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        }
    }

    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(true);

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        Environment env = app.run(args)
                             .getEnvironment();

        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                         "Local: \t\thttp://127.0.0.1:{}\n\t" +
                         "External: \thttp://{}:{}\n----------------------------------------------------------",
                 env.getProperty("server.port"),
                 InetAddress.getLocalHost()
                            .getHostAddress(),
                 env.getProperty("server.port"));

    }

}
