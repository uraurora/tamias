package com.sei.tamias.db.cache;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sei.tamias.db.entity.FileInfo;
import com.sei.tamias.service.IFileInfoService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-10 14:35
 * @description :
 */
@Service
@CacheConfig(cacheNames = {"fileCache"})
public class FileCache {

    @Resource
    private IFileInfoService fileInfoService;

    @Cacheable(key = "targetClass + methodName +#p0", condition = "#id != null")
    public FileInfo query(Long id){
        return fileInfoService.getById(id);
    }

    @Cacheable(key = "targetClass + methodName +#p0", condition = "#path != null")
    public FileInfo query(String path){
        return fileInfoService.getOne(Wrappers.<FileInfo>lambdaQuery()
                .eq(FileInfo::getPath, path)
        );
    }

}
