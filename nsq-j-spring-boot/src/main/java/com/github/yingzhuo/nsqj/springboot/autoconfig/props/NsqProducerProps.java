/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.springboot.autoconfig.props;

import com.github.yingzhuo.nsqj.spring.config.BatchConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "nsq.publisher")
public class NsqProducerProps {

    private boolean enabled = false;

    private String NsqdHost;

    private String failoverNsqdHost;

    private List<BatchConfig> batchConfigs;

    private ConfigProps config = new ConfigProps();

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "nsq.publisher.config")
    public static class ConfigProps {
        private String clientId;
        private String hostname;
        private Integer heartbeatInterval;
        private Integer outputBufferSize;
        private Integer outputBufferTimeout;
        private Boolean tlsV1;
        private Boolean snappy;
        private Boolean deflate;
        private Integer deflateLevel;
        private Integer sampleRate;
        private Integer msgTimeout;
        private Boolean featureNegotiation = true;
        private String userAgent = "nsq-j/1.0";
    }
}
