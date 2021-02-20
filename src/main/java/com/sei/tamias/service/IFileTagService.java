package com.sei.tamias.service;

import com.sei.tamias.db.entity.FileInfo;
import com.sei.tamias.db.entity.FileTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sei.tamias.util.Options;
import com.sun.istack.internal.NotNull;
import org.springframework.lang.NonNull;

import java.io.Serializable;
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
public interface IFileTagService extends IService<FileTag> {
    /**
     * 根据文件信息查找标签信息
     * @param fileId 文件信息id
     * @return 文件标签列表
     */
    List<FileTag> listByFileInfoId(@NonNull Long fileId);

    /**
     * 根据文件信息
     * @param idList 文件信息id列表
     * @return 由文件信息id映射标签列表的map
     */
    Map<Long, List<FileTag>> mapByFileInfoIds(@NonNull Collection<? extends Long> idList);

    /**
     * 根据文件信息查找标签信息
     * @param fileInfo 文件信息
     * @return 文件标签列表
     */
    List<FileTag> listByFileInfo(@NonNull FileInfo fileInfo);

    /**
     * 根据文件信息
     * @param files 文件信息id列表
     * @return 由文件信息id映射标签列表的map
     */
    Map<Long, List<FileTag>> mapByFileInfos(@NonNull Collection<? extends FileInfo> files);

    /**
     * 返回标签信息和对应关联的文件个数
     * @return
     */
    List<Options.Pair<FileTag, Integer>> countByTags();

    /**
     * tagId对应的标签关联的文件数
     * @param tagId 标签id
     * @return 文件数
     */
    Integer countByTag(@NonNull Long tagId);
}
