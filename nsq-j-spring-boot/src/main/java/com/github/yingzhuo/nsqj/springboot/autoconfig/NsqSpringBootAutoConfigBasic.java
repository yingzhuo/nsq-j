package com.github.yingzhuo.nsqj.springboot.autoconfig;

import com.github.yingzhuo.nsqj.spring.ClientFactoryBean;
import com.github.yingzhuo.nsqj.spring.config.BatchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;

import java.util.Optional;

public class NsqSpringBootAutoConfigBasic {

    @Autowired(required = false)
    public void config(FormatterRegistry registry) {
        Optional.ofNullable(registry).ifPresent(x -> x.addConverter(new BatchConfig.BatchConfigConverter()));
    }

    @Bean
    public ClientFactoryBean nsqClient() {
        return new ClientFactoryBean();
    }

}
