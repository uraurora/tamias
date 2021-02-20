package com.sei.tamias.service;

import com.sei.tamias.db.entity.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sei.tamias.db.entity.FileTag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sei.tamias.util.Options.*;

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

    /**
     * 对指定文件添加标签
     * @param fileId 文件id
     * @param tag 标签信息
     * @return 是否成功
     */
    boolean addTag(Long fileId, FileTag tag);

    /**
     * 对指定文件添加标签列表
     * @param fileId 文件id
     * @param tags 标签列表信息
     * @return 是否成功
     */
    boolean addTags(Long fileId, List<FileTag> tags);

    /**
     * 对指定文件添加标签列表
     * @param fileId 文件id
     * @param tags 标签列表信息
     * @return 是否成功
     */
    default boolean addTags(Long fileId, FileTag... tags){
        return addTags(fileId, listOf(tags));
    }


    boolean removeTag(Long fileId, Long tagId);

    boolean removeTags(Long fileId, Collection<? extends Long> tagId);


}