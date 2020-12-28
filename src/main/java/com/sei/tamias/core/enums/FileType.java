package com.sei.tamias.core.enums;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sei.tamias.util.Options;
import lombok.Getter;
import org.springframework.http.HttpEntity;

import static com.sei.tamias.core.global.ConstantsKt.*;
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
