package com.sei.tamias.db.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.sei.tamias.db.entity.FileInfo
import com.sei.tamias.db.entity.FileTag
import com.sei.tamias.db.entity.FileTagRelation
import com.sei.tamias.db.mapper.FileInfoMapper
import com.sei.tamias.db.service.IFileInfoService
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
class FileInfoServiceImpl : ServiceImpl<FileInfoMapper, FileInfo>(), IFileInfoService {

    @Resource
    lateinit var relationService: IFileTagRelationService

    override fun listByFileInfo(tag: FileTag): List<FileInfo> {
        return if(tag.id==null) emptyList() else listByFileId(tag.id)
    }

    override fun listByFileId(tagId: Long): List<FileInfo> {
        // 首先找到文件标签关联表中tagId相等的行
        val fileIds = relationService.lambdaQuery()
                .ge(FileTagRelation::getTagId, tagId)
                .list()
                .map { it.fileId }
        // 根据fileIds批量查信息
        return if(fileIds.isEmpty()) emptyList() else listByIds(fileIds)
    }

    override fun mapByTagIds(ids: MutableCollection<out Long>): Map<Long, List<FileInfo>> {
        TODO("Not yet implemented")
    }

    override fun mapByTags(ids: MutableCollection<out FileTag>): Map<Long, List<FileInfo>> {
        TODO("Not yet implemented")
    }

}