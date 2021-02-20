package com.sei.tamias.global.constnts;

import lombok.val;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2021-02-20 18:40
 * @description :
 */
public abstract class Constants {

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String nowDateTimeString() {
        return DATETIME_FORMATTER.format(LocalDateTime.now(ZoneOffset.of("+8")));
    }

    public static final String ALL_PATTERN = ".+";

    public static final String TEXT_PATTERN = "(.+)\\.txt";

    public static final String PICTURE_PATTERN = "(.+)\\.(png|jpe?g|bmp|gif|ico|pcx|tif|raw|tga)";

    public static final String VIDEO_PATTERN = "(.+)\\.(mpe?g|avi|rm(vb)?|mov|wmv|asf|dat)";

    public static final String MUSIC_PATTERN = "(.+)\\.(mp3|wma|rm|wav|mid)";

    /**
     * 用户id信息
     */
    public static final String USER_ID = "user_id";

    /**
     * 用户认证信息
     */
    public static final String AUTHORIZATION = "authorization";

    /**
     * 用户鉴权信息
     */
    public static final String AUTHENTICATION = "authentication";
}
