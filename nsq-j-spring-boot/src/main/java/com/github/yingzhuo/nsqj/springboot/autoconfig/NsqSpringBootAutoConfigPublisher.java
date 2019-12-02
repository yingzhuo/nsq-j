/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.springboot.autoconfig;

import com.github.yingzhuo.nsqj.Config;
import com.github.yingzhuo.nsqj.spring.PublisherFactoryBean;
import com.github.yingzhuo.nsqj.springboot.autoconfig.props.NsqProducerProps;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author 应卓
 * @since 1.0.0
 */
@EnableConfigurationProperties(NsqProducerProps.class)
@AutoConfigureAfter(NsqSpringBootAutoConfigBasic.class)
@ConditionalOnProperty(prefix = "nsq.publisher", name = "enabled", havingValue = "true")
public class NsqSpringBootAutoConfigPublisher {

    @Bean
    public PublisherFactoryBean publisher(NsqProducerProps props) {
        final PublisherFactoryBean factory = new PublisherFactoryBean();
        factory.setBatchConfigs(props.getBatchConfigs());
        factory.setNsqd(props.getNsqdHost());
        factory.setFailoverNsqd(props.getFailoverNsqdHost());

        Config cfg = new Config();
        cfg.setClientId(props.getConfig().getClientId());
        cfg.setHostname(props.getConfig().getHostname());
        cfg.setHeartbeatInterval(props.getConfig().getHeartbeatInterval());
        cfg.setOutputBufferSize(props.getConfig().getOutputBufferSize());
        cfg.setOutputBufferTimeout(props.getConfig().getOutputBufferTimeout());
        cfg.setTlsV1(props.getConfig().getTlsV1());
        cfg.setSnappy(props.getConfig().getSnappy());
        cfg.setDeflate(props.getConfig().getDeflate());
        cfg.setSampleRate(props.getConfig().getSampleRate());
        cfg.setMsgTimeout(props.getConfig().getMsgTimeout());
        cfg.setFeatureNegotiation(props.getConfig().getFeatureNegotiation());
        cfg.setUserAgent(props.getConfig().getUserAgent());
        factory.setConfig(cfg);

        return factory;
    }

}
