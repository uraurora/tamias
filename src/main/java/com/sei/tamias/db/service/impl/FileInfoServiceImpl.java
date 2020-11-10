package com.sei.tamias.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.sei.tamias.db.cache.FileCache;
import com.sei.tamias.db.cache.FileTagRelationCache;
import com.sei.tamias.db.cache.TagCache;
import com.sei.tamias.db.entity.FileInfo;
import com.sei.tamias.db.entity.FileTag;
import com.sei.tamias.db.entity.FileTagRelation;
import com.sei.tamias.db.mapper.FileInfoMapper;
import com.sei.tamias.db.service.IFileInfoService;
import com.sei.tamias.db.service.IFileTagRelationService;
import com.sei.tamias.db.service.IFileTagService;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private TagCache tagCache;

    @Resource
    private FileCache fileCache;

    @Resource
    private FileTagRelationCache relationCache;

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
                .map(fileCache::query)
                .map(fileInfo -> {
                    if(tag == null || tag.getId() == null){
                        return false;
                    }
                    FileTag byId = tagCache.query(tag.getId());
                    // 说明库中没有此标签，添加标签后绑定
                    if(byId == null){
                        // 存储后实体类中会置id信息
                        tagService.save(tag);
                        relationService.save(FileTagRelation.from(fileId, tag.getId()));
                    }else{
                        // 有标签，有文件，则判断关系表中是否有对应记录
                        if(relationCache.isRelated(fileId, tag.getId())){
                            relationService.save(FileTagRelation.from(fileId, tag.getId()));
                        }
                    }
                    return true;
                })
                .getOrDefault(false);

    }

    @Override
    public boolean addTags(Long fileId, List<FileTag> tags) {
        return Option.of(fileId)
                .map(fileCache::query)
                .map(fileInfo -> {
                    if(isEmpty(tags)){
                        return false;
                    }
                    Set<Long> ids = tags.stream().map(FileTag::getId).collect(toSet());
                    // 表里已经有的标签
                    final Set<Long> fileTagIds = tagService.listByIds(ids).stream()
                            .filter(Objects::nonNull)
                            .map(FileTag::getId)
                            .collect(toSet());
                    final Set<Long> needSaveTagIds = Sets.difference(ids, fileTagIds);
                    final Set<FileTag> needSaveTags = tags.stream().filter(t->ids.contains(t.getId())).collect(toSet());


                    // 这些是需要新增
                    final List<FileTagRelation> relations = fileTagIds.stream()
                            .filter(Objects::nonNull)
                            .map(it->new FileTagRelation().setFileId(fileId).setTagId(it))
                            .collect(toList());
                    if(isNotEmpty(needSaveTagIds)){
                        tagService.saveBatch(needSaveTags);
                        // todo:
                    }
                    return true;
                })
                .getOrDefault(false);
    }

    @Override
    public boolean addTags(Long fileId, FileTag... tags) {
        return addTags(fileId, listOf(tags));
    }
}
