package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author Godpalce
 * @date 2025/8/8
 * @description 全局初始化配置
 */
@Configuration
@Slf4j
public class InitConfig {
    
    public InitConfig() {
        log.info("全局初始化完成（支付宝功能暂未配置）");
    }
}
