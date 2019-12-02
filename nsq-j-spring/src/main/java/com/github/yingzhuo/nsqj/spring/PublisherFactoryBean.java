/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.spring;

import com.github.yingzhuo.nsqj.Client;
import com.github.yingzhuo.nsqj.Config;
import com.github.yingzhuo.nsqj.Publisher;
import com.github.yingzhuo.nsqj.spring.config.BatchConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @author 应卓
 * @since 1.0.0
 */
public class PublisherFactoryBean implements FactoryBean<Publisher>, InitializingBean {

    private Client client;
    private String nsqd = "localhost";
    private String failoverNsqd;
    private Config config;
    private List<BatchConfig> batchConfigs;
    private int FailoverDurationSecs = 300;
    private PublisherConfigurer publisherConfigurer;

    public Publisher getObject() {
        final Publisher publisher = new Publisher(client, nsqd, failoverNsqd);

        if (config != null) {
            publisher.setConfig(config);
        }

        if (batchConfigs != null) {
            for (BatchConfig cfg : batchConfigs) {
                publisher.setBatchConfig(cfg.getTopic(), cfg.getMaxSizeBytes(), cfg.getMaxDelayMillis());
            }
        }

        publisher.setFailoverDurationSecs(FailoverDurationSecs);

        if (this.publisherConfigurer != null) {
            publisherConfigurer.config(publisher);
        }

        return publisher;
    }

    @Override
    public Class<?> getObjectType() {
        return Publisher.class;
    }

    @Override
    public void afterPropertiesSet() {
        if (client == null) {
            client = Client.getDefaultClient();
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setNsqd(String nsqd) {
        this.nsqd = nsqd;
    }

    public void setFailoverNsqd(String failoverNsqd) {
        this.failoverNsqd = failoverNsqd;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setBatchConfigs(List<BatchConfig> batchConfigs) {
        this.batchConfigs = batchConfigs;
    }

    public void setFailoverDurationSecs(int failoverDurationSecs) {
        FailoverDurationSecs = failoverDurationSecs;
    }

    public void setPublisherConfigurer(PublisherConfigurer publisherConfigurer) {
        this.publisherConfigurer = publisherConfigurer;
    }

}
