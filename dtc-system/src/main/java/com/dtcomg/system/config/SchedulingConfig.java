package com.dtcomg.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务配置类
 * 配置Spring Task的任务调度器，优化定时任务执行性能
 * 
 * @author 系统管理员
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    /**
     * 配置任务调度线程池
     * 使用线程池可以避免任务阻塞，提高定时任务执行效率
     * 
     * @return 任务调度器
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置线程池大小
        scheduler.setPoolSize(5);
        // 设置线程名称前缀
        scheduler.setThreadNamePrefix("scheduled-task-");
        // 设置线程池关闭时等待所有任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置任务的等待时间，避免因等待时间过长导致关闭超时
        scheduler.setAwaitTerminationSeconds(60);
        // 初始化调度器
        scheduler.initialize();
        return scheduler;
    }
}