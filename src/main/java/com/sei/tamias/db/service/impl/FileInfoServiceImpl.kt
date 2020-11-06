package com.sei.tamias.db.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.sei.tamias.db.entity.FileInfo
import com.sei.tamias.db.entity.FileTag
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
class FileInfoServiceImpl : ServiceImpl<FileInfoMapper?, FileInfo?>(), IFileInfoService {

    @Resource
    lateinit var relationService: IFileTagRelationService

    override fun listByFileInfo(tag: FileTag?): MutableList<FileInfo> {
        TODO("Not yet implemented")
    }

    override fun listByFileId(tagId: Long?): MutableList<FileInfo> {
        TODO("Not yet implemented")
    }

    override fun mapByTagIds(ids: MutableCollection<out Long>?): MutableMap<Long, MutableList<FileInfo>> {
        TODO("Not yet implemented")
    }

    override fun mapByTags(ids: MutableCollection<out FileTag>?): MutableMap<Long, MutableList<FileInfo>> {
        TODO("Not yet implemented")
    }

}