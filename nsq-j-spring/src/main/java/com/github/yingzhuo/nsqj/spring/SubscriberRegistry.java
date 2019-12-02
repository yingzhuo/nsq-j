/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj.spring;

import com.github.yingzhuo.nsqj.DirectSubscriber;
import com.github.yingzhuo.nsqj.Message;
import com.github.yingzhuo.nsqj.MessageHandler;
import com.github.yingzhuo.nsqj.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * @author 应卓
 * @since 1.0.0
 */
@Slf4j
public class SubscriberRegistry implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        for (Method method : bean.getClass().getMethods()) {

            NsqSubscriber annotation = method.getAnnotation(NsqSubscriber.class);
            if (annotation != null) {
                register(annotation, method, bean);
            }
        }

        return bean;
    }

    private void register(NsqSubscriber annotation, Method method, final Object bean) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1 || parameterTypes[0] != Message.class) {
            return;
        }

        String topic = annotation.topic();
        String ch = annotation.channel();
        String[] nsqdHosts = annotation.nsqdHosts();
        String[] lookupHosts = annotation.lookupHosts();
        int lookupIntervalSecs = annotation.lookupIntervalSecs();
        int maxLookupFailuresBeforeError = annotation.maxLookupFailuresBeforeError();

        if (lookupHosts.length == 0) {
            log.debug("register DirectSubscriber topic={} channel={}", topic, ch);
            Subscriber subscriber = new DirectSubscriber(lookupIntervalSecs, nsqdHosts);
            subscriber.subscribe(topic, ch, new MessageHandler() {
                @Override
                public void accept(Message msg) {
                    try {
                        method.invoke(bean, msg);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
            return;
        }

        log.debug("register Subscriber topic={} channel={}", topic, ch);
        Subscriber subscriber = new Subscriber(lookupIntervalSecs, maxLookupFailuresBeforeError, lookupHosts);
        subscriber.subscribe(topic, ch, new MessageHandler() {
            @Override
            public void accept(Message msg) {
                try {
                    method.invoke(bean, msg);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

}
