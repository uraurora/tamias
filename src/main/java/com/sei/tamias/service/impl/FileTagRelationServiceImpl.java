package com.sei.tamias.service.impl;

import com.sei.tamias.db.entity.FileTagRelation;
import com.sei.tamias.db.mapper.FileTagRelationMapper;
import com.sei.tamias.service.IFileTagRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sei
 * @since 2020-11-06
 */
@Service
public class FileTagRelationServiceImpl extends ServiceImpl<FileTagRelationMapper, FileTagRelation> implements IFileTagRelationService {

    @Override
    public boolean isRelated(Long fileId, Long tagId){
        return this.lambdaQuery()
                .eq(FileTagRelation::getFileId, fileId)
                .eq(FileTagRelation::getTagId, tagId)
                .count() > 0;
    }
}
