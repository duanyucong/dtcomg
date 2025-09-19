package com.dtcomg.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupBanner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupBanner.class);

    private final Environment environment;

    public StartupBanner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String appName = environment.getProperty("spring.application.name", "dtcomg");
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String localUrl = String.format("http://localhost:%s%s", port, contextPath);

        logger.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}\n\t" +
                "Swagger UI: \t{}/swagger-ui/swagger-ui/index.html\n" +
                "----------------------------------------------------------",
                appName, localUrl, localUrl);
    }
}
