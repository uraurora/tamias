package com.sei.tamias.service;

import com.sei.tamias.db.entity.FileTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sei
 * @since 2020-11-06
 */
public interface IFileTagRelationService extends IService<FileTagRelation> {
    boolean isRelated(Long fileId, Long tagId);
}
