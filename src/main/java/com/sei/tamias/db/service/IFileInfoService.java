package com.sei.tamias.db.service;

import com.sei.tamias.db.entity.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sei.tamias.db.entity.FileTag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sei
 * @since 2020-11-06
 */
public interface IFileInfoService extends IService<FileInfo> {
    /**
     * 根据标签找符合的文件列表
     * @param tag 文件标签信息
     * @return 文件列表
     */
    List<FileInfo> listByFileInfo(FileTag tag);

    /**
     * 根据标签列表找文件
     * @param tagId 标签id
     * @return 文件的列表
     */
    List<FileInfo> listByFileId(Long tagId);

    Map<Long, List<FileInfo>> mapByTagIds(Collection<? extends Long> ids);

    Map<Long, List<FileInfo>> mapByTags(Collection<? extends FileTag> ids);
}