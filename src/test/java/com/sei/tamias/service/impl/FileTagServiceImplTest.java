package com.sei.tamias.service.impl;

import com.sei.tamias.db.entity.FileTag;
import com.sei.tamias.service.IFileTagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FileTagServiceImplTest {

    @Resource
    IFileTagService tagService;

    @Test
    public void test(){
        tagService.save(new FileTag().setName("hhhh").setUserId(12));
    }
}