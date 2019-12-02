/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.spring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.convert.converter.Converter;

@Getter
@Setter
public class BatchConfig {

    private String topic;
    private int maxSizeBytes;
    private int maxDelayMillis;

    public static class BatchConfigConverter implements Converter<String, BatchConfig> {

        @Override
        public BatchConfig convert(String source) {

            if (source == null) {
                throw new IllegalArgumentException("invalid batch config (null)");
            }

            String[] parts = source.split(",");

            if (parts.length != 3) {
                throw new IllegalArgumentException("invalid batch config");
            }

            try {
                final BatchConfig cfg = new BatchConfig();
                cfg.topic = parts[0].trim();
                cfg.maxSizeBytes = Integer.parseInt(parts[1].trim());
                cfg.maxDelayMillis = Integer.parseInt(parts[2].trim());
                return cfg;
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

}
