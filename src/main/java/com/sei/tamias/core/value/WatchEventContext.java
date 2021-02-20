package com.sei.tamias.core.value;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-31 17:27
 * @description : 文件变更上下文数据类，包含文件变更类型，文件名，时间，和具体路径
 */
public class WatchEventContext<T> {

    private WatchEvent<T> event;

    private Path dir;

    private LocalDateTime dateTime = LocalDateTime.now();

    public WatchEvent<T> getEvent() {
        return event;
    }

    public void setEvent(WatchEvent<T> event) {
        this.event = event;
    }

    public Path getDir() {
        return dir;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchEventContext<?> that = (WatchEventContext<?>) o;
        return Objects.equals(event, that.event) &&
                Objects.equals(dir, that.dir) &&
                Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, dir, dateTime);
    }

    @Override
    public String toString() {
        return "WatchEventContext{" +
                "eventType=" + event.kind() +
                ", dir=" + dir +
                ", dateTime=" + dateTime +
                '}';
    }
}
