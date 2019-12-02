/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.spring;

import java.lang.annotation.*;

/**
 * @author 应卓
 * @since 1.0.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NsqSubscriber {

    public String topic();

    public String channel();

    public int lookupIntervalSecs() default 60;

    public int maxLookupFailuresBeforeError() default 5;

    public String[] lookupHosts() default {};

    public String[] nsqdHosts() default {};

}
