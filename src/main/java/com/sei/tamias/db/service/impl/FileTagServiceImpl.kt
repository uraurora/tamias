package com.sei.tamias.db.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.sei.tamias.db.entity.FileInfo
import com.sei.tamias.db.entity.FileTag
import com.sei.tamias.db.entity.FileTagRelation
import com.sei.tamias.db.mapper.FileTagMapper
import com.sei.tamias.db.service.IFileTagRelationService
import com.sei.tamias.db.service.IFileTagService
import org.springframework.stereotype.Service
import javax.annotation.Resource

/**
 *
 *
 * 服务实现类
 *
 *
 * @author sei
 * @since 2020-11-06
 */
@Service
class FileTagServiceImpl : ServiceImpl<FileTagMapper, FileTag>(), IFileTagService {

    @Resource
    lateinit var relationService: IFileTagRelationService

    override fun listByFileInfoId(fileId: Long): List<FileTag> {
        // 首先找到文件标签关联表中fileId相等的行
        val tagIds = relationService.lambdaQuery()
                .ge(FileTagRelation::getFileId, fileId)
                .list()
                .map { it.tagId }
        // 根据tagIds批量传tag信息
        return if(tagIds.isEmpty()) emptyList() else listByIds(tagIds)
    }

    override fun mapByFileInfoIds(idList: Collection<Long>): Map<Long, List<FileTag>> {
        val tagMap: Map<Long, List<FileTagRelation>> = relationService.lambdaQuery()
                .select(FileTagRelation::getFileId, FileTagRelation::getTagId)
                .`in`(FileTagRelation::getFileId, idList)
                .list()
                .groupBy { it.fileId }
        return if(tagMap.isEmpty()) emptyMap() else {
            mutableMapOf<Long, List<FileTag>>().apply{
                tagMap.forEach { (k, v) ->
                    val s = if(v.isEmpty()) emptyList() else listByIds(v.map { it.tagId })
                    put(k, s)
                }
            }
        }
    }

    override fun listByFileInfo(fileInfo: FileInfo): List<FileTag> {
        return  if(fileInfo.id==null) emptyList() else listByFileInfoId(fileId = fileInfo.id)
    }

    override fun mapByFileInfos(files: Collection<FileInfo>): Map<Long, List<FileTag>> {
        val ids = files.map { it.id }
        return if(ids.isEmpty()) emptyMap() else mapByFileInfoIds(ids)
    }

    override fun countByTags(): List<Pair<FileTag, Int>> {
        TODO("Not yet implemented")

    }

    override fun countByTag(tagId: Long?): Int {
        return relationService.lambdaQuery()
                .eq(FileTagRelation::getTagId, tagId)
                .count()
    }

}