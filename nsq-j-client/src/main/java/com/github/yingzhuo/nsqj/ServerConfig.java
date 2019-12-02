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
 * nsqd settings that can't be changed by the client.
 * returned in response to IDENTIFY command.
 *
 * @author 应卓
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ServerConfig extends Config {

    private String version;
    private Integer maxRdyCount;
    private Integer maxMsgTimeout;
    private Integer maxDeflateLevel;
    private Boolean authRequired;

}
