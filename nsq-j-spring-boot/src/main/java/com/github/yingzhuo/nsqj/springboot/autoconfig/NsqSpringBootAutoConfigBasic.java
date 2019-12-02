/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.springboot.autoconfig;

import com.github.yingzhuo.nsqj.spring.ClientFactoryBean;
import com.github.yingzhuo.nsqj.spring.config.BatchConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;

/**
 * @author 应卓
 * @since 1.0.0
 */
@ConditionalOnExpression("'${nsq.publisher.enabled}' == 'true' or '${nsq.subscriber.enabled}' == 'true'")
public class NsqSpringBootAutoConfigBasic {

    @Bean
    @ConfigurationPropertiesBinding
    public BatchConfig.BatchConfigConverter batchConfigConverter() {
        return new BatchConfig.BatchConfigConverter();
    }

    @Bean
    public ClientFactoryBean nsqClient() {
        return new ClientFactoryBean();
    }

}
