package team9.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


@Configuration
public class ExecutorConfig {

    @Bean(name = "ioExecutor")
    public ExecutorService ioExecutor() {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        return executor;
    }
}