package com.atguigu.mediaplayer.domain;

/**
 * Created by Administrator on 2017/5/26.
 */

public class Lyric {

    private String content;

    private Long timePoint;

    private Long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(Long timePoint) {
        this.timePoint = timePoint;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
