/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.springboot.autoconfig;

import com.github.yingzhuo.nsqj.spring.SubscriberRegistry;
import com.github.yingzhuo.nsqj.springboot.autoconfig.props.NsqSubscriberProps;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(NsqSubscriberProps.class)
@AutoConfigureAfter(NsqSpringBootAutoConfigBasic.class)
@ConditionalOnProperty(prefix = "nsq.subscriber", name = "enabled", havingValue = "true")
public class NsqSpringBootAutoConfigSubscriber {

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new SubscriberRegistry();
    }

}
