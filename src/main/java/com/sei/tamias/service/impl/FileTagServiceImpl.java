package com.sei.tamias.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sei.tamias.db.entity.FileInfo;
import com.sei.tamias.db.entity.FileTag;
import com.sei.tamias.db.entity.FileTagRelation;
import com.sei.tamias.db.mapper.FileTagMapper;
import com.sei.tamias.service.IFileTagRelationService;
import com.sei.tamias.service.IFileTagService;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sei.tamias.util.Options.*;
import static java.util.stream.Collectors.*;
/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-09 17:05
 * @description :
 */
@Service
public class FileTagServiceImpl extends ServiceImpl<FileTagMapper, FileTag> implements IFileTagService {

    @Resource
    private IFileTagRelationService relationService;

    @Override
    public List<FileTag> listByFileInfoId(@NotNull Long fileId) {
        // 首先找到文件标签关联表中fileId相等的行
        List<Long> tagIds = relationService.lambdaQuery()
                .ge(FileTagRelation::getFileId, fileId)
                .list()
                .stream()
                .map(FileTagRelation::getTagId)
                .collect(toList());
        // 根据tagIds批量传tag信息
        return tagIds.isEmpty()? emptyList() : listByIds(tagIds);
    }

    @Override
    public Map<Long, List<FileTag>> mapByFileInfoIds(@NotNull Collection<? extends Long> idList) {
        Map<Long, List<FileTagRelation>> tagMap = relationService.lambdaQuery()
                .select(FileTagRelation::getFileId, FileTagRelation::getTagId)
                .in(FileTagRelation::getFileId, idList)
                .list()
                .stream()
                .collect(groupingBy(FileTagRelation::getFileId));

        if(tagMap.isEmpty()) {
            return emptyMap();
        } else {
            return buildMap(m-> tagMap.forEach((k, v)->{
                List<Long> ids = v.stream().map(FileTagRelation::getTagId).collect(toList());
                List<FileTag> s = v.isEmpty()? emptyList() : listByIds(ids);
                m.put(k, s);
            }));
        }
    }

    @Override
    public List<FileTag> listByFileInfo(@NotNull FileInfo fileInfo) {
        return fileInfo.getId()==null? emptyList() : listByFileInfoId(fileInfo.getId());
    }

    @Override
    public Map<Long, List<FileTag>> mapByFileInfos(@NotNull Collection<? extends FileInfo> files) {
        List<Long> ids = files.stream().map(FileInfo::getId).collect(toList());
        return ids.isEmpty()? emptyMap() : mapByFileInfoIds(ids);
    }

    @Override
    public List<Pair<FileTag, Integer>> countByTags() {
        // todo:需要从关系表中搜索，根据tagId进行groupBy返回找到对应文件的个数
        return null;
    }

    @Override
    public Integer countByTag(@NotNull Long tagId) {
        return relationService.lambdaQuery()
                .eq(FileTagRelation::getTagId, tagId)
                .count();
    }
}
