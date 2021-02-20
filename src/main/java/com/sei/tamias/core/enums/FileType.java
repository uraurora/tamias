package com.sei.tamias.core.enums;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Getter;

import static com.sei.tamias.global.constnts.Constants.*;
import static com.sei.tamias.util.Options.Option;

@Getter
enum FileType {
    /**
     * 视频
     */
    VIDEO(VIDEO_PATTERN){
        @Override
        <R> Option<R> transform() {
            return null;
        }
    },
    /**
     * 文本
     */
    TEXT(TEXT_PATTERN){
        @Override
        <R> Option<R> transform() {
            return null;
        }
    },
    /**
     * 图片
     */
    PICTURE(PICTURE_PATTERN){
        @Override
        <R> Option<R> transform() {
            return null;
        }
    },
    /**
     * 音乐
     */
    MUSIC(MUSIC_PATTERN){
        @Override
        <R> Option<R> transform() {
            return null;
        }
    },
    /**
     * 所有文件
     */
    ALL(ALL_PATTERN){
        @Override
        <R> Option<R> transform() {
            return null;
        }
    }
    ;

    private final String pattern;

    FileType(String pattern) {
        this.pattern = pattern;
    }

    public static FileType from(String pattern){
        if(StringUtils.isBlank(pattern)){
            return null;
        }
        for (FileType value : values()) {
            if(value.pattern.equals(pattern)){
                return value;
            }
        }
        return null;
    }

    abstract <R> Option<R> transform();

}
