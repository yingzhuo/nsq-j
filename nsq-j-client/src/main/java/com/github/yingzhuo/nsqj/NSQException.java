/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

/**
 * @author 应卓
 * @since 1.0.0
 */
public class NSQException extends RuntimeException {

    public NSQException(String message) {
        super(message);
    }

    public NSQException(String message, Throwable cause) {
        super(message, cause);
    }

}
