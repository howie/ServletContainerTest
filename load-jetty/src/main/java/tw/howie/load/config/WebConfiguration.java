package tw.howie.load.config;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author howie
 * @since 2015/4/1
 */
@Configuration
public class WebConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "jetty.");
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        factory.addServerCustomizers(jettyServerCustomizer());
        //TODO
        factory.setSessionTimeout(10, TimeUnit.MINUTES);
        return factory;
    }

    @Bean
    public JettyServerCustomizer jettyServerCustomizer() {

        return server -> {

            threadPool(server);
            lowResourcesMonitor(server);
            mBeanContainer(server);

        };
    }

    private void threadPool(Server server) {

        final int maxThreads = propertyResolver.getProperty("maxThreads", Integer.class, 500);
        final int minThreads = propertyResolver.getProperty("minThreads", Integer.class, 50);
        final int idleTimeout = propertyResolver.getProperty("idleTimeout", Integer.class, 10 * 1000);

        Object[] data = {maxThreads, minThreads, idleTimeout};
        log.info("JettyServerCustomizer maxThreads:{} minThreads:{} idleTimeout:{}", data);
        // Tweak the connection pool used by Jetty to handle incoming HTTP connections
        final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
        threadPool.setMaxThreads(maxThreads);
        threadPool.setMinThreads(minThreads);
        threadPool.setIdleTimeout(idleTimeout);
        threadPool.setName("jetty");
        threadPool.setDaemon(true);
        threadPool.setThreadsPriority(Thread.MAX_PRIORITY);
    }

    /**
     * http://www.eclipse.org/jetty/documentation/current/limit-load.html
     *
     * @return
     */

    private void lowResourcesMonitor(Server server) {

        log.info("Create Jetty lowResourcesMonitor....");

        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);

    }

    /**
     * Expose Jetty managed beans to the JMX platform server provided by Spring
     * http://eclipse.org/jetty/documentation/current/jmx-chapter.html
     * @param server
     */
    private void mBeanContainer(Server server) {

        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addEventListener(mbContainer);
        server.addBean(mbContainer);

        // Add loggers MBean to server (will be picked up by MBeanContainer above)
        server.addBean(Log.getLog());
    }
}
