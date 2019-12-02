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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class ClientFactoryBean implements FactoryBean<Client>, InitializingBean, DisposableBean {

    private Client client;

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    @Override
    public Client getObject() {
        return this.client;
    }

    @Override
    public void afterPropertiesSet() {
        this.client = Client.getDefaultClient();
    }

    @Override
    public void destroy() {
        log.debug("client stopping ...");
        this.client.stop();
    }

}
