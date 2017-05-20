package com.atguigu.mediaplayer.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LocalMediaBean implements Serializable {
    private String name;
    private long duration;
    private long size;
    private String address;

    public LocalMediaBean() {
    }

    public LocalMediaBean(String name, long duration, long size, String address) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "LocalMediaBean{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", address='" + address + '\'' +
                '}';
    }
}
