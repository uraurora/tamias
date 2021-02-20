package com.sei.tamias.db.cache;

import com.sei.tamias.db.entity.FileTagRelation;
import com.sei.tamias.service.IFileTagRelationService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-10 16:09
 * @description :
 */
@Service
@CacheConfig(cacheNames = {"relationCache"})
public class FileTagRelationCache {
    @Resource
    private IFileTagRelationService relationService;

    @Cacheable(key = "targetClass + methodName + #p0 + #p1", condition = "#fileId != null && #tagId != null")
    public boolean isRelated(Long fileId, Long tagId){
        return relationService.lambdaQuery()
                .eq(FileTagRelation::getFileId, fileId)
                .eq(FileTagRelation::getTagId, tagId)
                .count() > 0;
    }
}
