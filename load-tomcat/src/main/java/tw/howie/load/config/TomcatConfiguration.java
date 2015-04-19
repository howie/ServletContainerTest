package tw.howie.load.config;

import org.apache.catalina.core.AprLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author howie
 * @since 2015/4/15
 */
@Configuration
public class TomcatConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(TomcatConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "tomcat.");
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        String protocol = propertyResolver.getProperty("protocol");
        log.info("Tomcat Protocol:{}", protocol);
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setProtocol(protocol);

        factory.addContextLifecycleListeners(new AprLifecycleListener());
        factory.setSessionTimeout(10, TimeUnit.MINUTES);
        List<TomcatConnectorCustomizer> cs = new ArrayList();
        cs.add(tomcatConnectorCustomizers());
        factory.setTomcatConnectorCustomizers(cs);
        return factory;
    }

    @Bean
    public TomcatConnectorCustomizer tomcatConnectorCustomizers() {

        String maxThreads = propertyResolver.getProperty("maxThreads");
        String acceptCount = propertyResolver.getProperty("acceptCount");
        return connector -> {
            connector.setAttribute("maxThreads", maxThreads);
            connector.setAttribute("acceptCount", acceptCount);
        };

    }
}
