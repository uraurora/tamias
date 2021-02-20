package com.sei.tamias.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.sei.tamias.db.cache.FileCache;
import com.sei.tamias.db.cache.FileTagRelationCache;
import com.sei.tamias.db.cache.TagCache;
import com.sei.tamias.db.entity.FileInfo;
import com.sei.tamias.db.entity.FileTag;
import com.sei.tamias.db.entity.FileTagRelation;
import com.sei.tamias.db.mapper.FileInfoMapper;
import com.sei.tamias.service.IFileInfoService;
import com.sei.tamias.service.IFileTagRelationService;
import com.sei.tamias.service.IFileTagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.sei.tamias.util.Options.*;
import static java.util.stream.Collectors.*;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-09 17:30
 * @description :
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {

    @Resource
    private IFileTagRelationService relationService;

    @Resource
    private IFileTagService tagService;

    @Resource
    private IFileInfoService fileInfoService;


    @Override
    public List<FileInfo> listByFileInfo(FileTag tag) {
        return tag.getId() ==null? emptyList() : listByFileId(tag.getId());
    }

    @Override
    public List<FileInfo> listByFileId(Long tagId) {
        // 首先找到文件标签关联表中tagId相等的行
        List<Long> fileIds = relationService.lambdaQuery()
                .ge(FileTagRelation::getTagId, tagId)
                .list()
                .stream()
                .map(FileTagRelation::getFileId)
                .collect(toList());
        // 根据fileIds批量查信息
        return isEmpty(fileIds)? emptyList() : listByIds(fileIds);
    }

    @Override
    public Map<Long, List<FileInfo>> mapByTagIds(Collection<? extends Long> ids) {
        return null;
    }

    @Override
    public Map<Long, List<FileInfo>> mapByTags(Collection<? extends FileTag> ids) {
        return null;
    }

    @Override
    public boolean addTag(Long fileId, FileTag tag) {
        return Option.of(fileId)
                .map(fileInfoService::getById)
                .map(fileInfo -> {
                    if(fileInfo == null){
                        return false;
                    }else{
                        // 如果tag为null，返回false
                        if(tag == null){
                            return false;
                        }
                        // 如果id为null，先存储tag信息
                        if(tag.getId() == null){
                            tagService.save(tag);
                        }
                        // 有标签，有文件，则判断关系表中是否有对应记录
                        if(tag.getId() != null && !relationService.isRelated(fileId, tag.getId())){
                            relationService.save(FileTagRelation.from(fileId, tag.getId()));
                        }
                        return true;
                    }
                })
                .getOrDefault(false);

    }

    @Override
    public boolean addTags(Long fileId, List<FileTag> tags) {
        return Option.of(fileId)
                .map(fileInfoService::getById)
                .map(fileInfo -> {
                    if(isEmpty(tags) || fileInfo == null){
                        return false;
                    }
                    final Set<FileTag> nullIdTags = tags.stream()
                            .filter(tag -> tag != null && tag.getId() == null)
                            .collect(toSet());
                    tagService.saveBatch(nullIdTags);

                    final List<FileTagRelation> relations = tags.stream()
                            .filter(tag->tag != null && tag.getId() != null)
                            .map(tag->FileTagRelation.from(fileId, tag.getId()))
                            .collect(toList());
                    relationService.saveBatch(relations);
                    return true;
                })
                .getOrDefault(false);
    }

    @Override
    public boolean removeTag(Long fileId, Long tagId) {
        return Option.of(fileId)
                .map(fileInfoService::getById)
                .map(fileInfo -> {
                    if(fileInfo == null){
                        return false;
                    }else{
                        final List<Long> relationIds = relationService.lambdaQuery()
                                .eq(FileTagRelation::getTagId, tagId)
                                .list().stream()
                                .map(FileTagRelation::getId)
                                .collect(toList());
                        relationService.removeByIds(relationIds);
                        return true;
                    }
                })
                .getOrDefault(false);
    }

    @Override
    public boolean removeTags(Long fileId, Collection<? extends Long> tagId) {
        return false;
    }

    private void notSupported() throws IllegalAccessException {
        throw new IllegalAccessException("not supported yet!");
    }

}
