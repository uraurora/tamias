package com.sei.tamias.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.sei.tamias.global.ExtensionsKt.nowDateTimeString;

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
public class FileTagRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fileId;

    private Long tagId;

    private String createTime = nowDateTimeString();

    private String updateTime = nowDateTimeString();

    public static FileTagRelation from(Long fileId, Long tagId){
        return new FileTagRelation()
                .setFileId(fileId)
                .setTagId(tagId);
    }


}
