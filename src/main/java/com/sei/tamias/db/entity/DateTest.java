package com.sei.tamias.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sei.tamias.global.constnts.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author sei
 * @since 2020-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("date_test")
public class DateTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;

    private String time = Constants.DATETIME_FORMATTER.format(LocalDateTime.now(ZoneOffset.of("+8")));

    public int component1() {
        return id;
    }

    @NonNull
    public LocalDateTime component2() {
        return LocalDateTime.parse(time, Constants.DATETIME_FORMATTER);
    }
}
