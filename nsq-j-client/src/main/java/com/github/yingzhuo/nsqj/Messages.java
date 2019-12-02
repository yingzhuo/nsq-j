/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  _   _ ____   ___            _
 * | \ | / ___| / _ \          | |
 * |  \| \___ \| | | |_____ _  | |
 * | |\  |___) | |_| |_____| |_| |
 * |_| \_|____/ \__\_\      \___/                                           https://github.com/yingzhuo/nsq-j
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.github.yingzhuo.nsqj;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 应卓
 * @since 1.0.0
 */
public final class Messages implements Iterable<byte[]> {

    private List<byte[]> array = new LinkedList<>();

    public static Messages of(List<byte[]> msgArray) {
        Messages result = new Messages();
        result.array = msgArray;
        return result;
    }

    public static Messages of(String... msgArray) {
        Messages result = new Messages();
        result.array = Arrays.stream(msgArray).map(it -> it.getBytes(StandardCharsets.UTF_8)).collect(Collectors.toList());
        return result;
    }

    public static Messages newInstance() {
        return new Messages();
    }

    private Messages() {
    }

    public Messages append(String data) {
        return append(data.getBytes(StandardCharsets.UTF_8));
    }

    public Messages append(byte[] data) {
        array.add(data);
        return this;
    }

    public Messages append(Iterable<String> dataIterable) {
        for (String it : dataIterable) {
            append(it);
        }
        return this;
    }

    public Messages append(Messages another) {
        for (byte[] it : another) {
            append(it);
        }
        return this;
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }

    List<byte[]> getData() {
        return this.array;
    }

    @Override
    public Iterator<byte[]> iterator() {
        return array.iterator();
    }

}
