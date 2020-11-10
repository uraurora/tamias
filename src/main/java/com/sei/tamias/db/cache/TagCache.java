package com.sei.tamias.db.cache;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sei.tamias.db.entity.FileTag;
import com.sei.tamias.db.service.IFileTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-09 23:14
 * @description :
 */
@Service
@CacheConfig(cacheNames = {"tagCache"})
public class TagCache {

    @Resource
    private IFileTagService tagService;

    @Cacheable(key = "targetClass + methodName +#p0", condition = "#id != null")
    public FileTag query(Long id){
    return tagService.getById(id);
    }

    @Cacheable(key = "targetClass + methodName + #p0 + #p1", condition = "#name != null && #userId != null")
    public FileTag query(String name, Long userId){
        return tagService.getOne(Wrappers.<FileTag>lambdaQuery()
                .eq(FileTag::getName, name)
                .eq(FileTag::getUserId, userId)
        );
    }



}
