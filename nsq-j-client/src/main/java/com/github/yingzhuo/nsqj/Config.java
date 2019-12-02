/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

import lombok.*;

/**
 * Configuration sent to nsqd with the IDENTIFY command
 * http://nsq.io/clients/tcp_protocol_spec.html#identify
 * to negotiate the features to use on a connection.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Config implements java.io.Serializable {

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
