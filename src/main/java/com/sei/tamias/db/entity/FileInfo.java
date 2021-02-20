package com.sei.tamias.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.sei.tamias.global.constnts.Constants.nowDateTimeString;

/**
 * <p>
 * 
 * </p>
 *
 * @author sei
 * @since 2020-11-06
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer size;

    private String path;

    private String name;

    private Integer listenerId;

    private String fileCreationTime;

    private String fileLastModifiedTime;

    private Integer isDict;

    private String createTime = nowDateTimeString();

    private String updateTime = nowDateTimeString();


}
