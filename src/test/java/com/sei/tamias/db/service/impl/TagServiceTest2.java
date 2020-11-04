package com.sei.tamias.db.service.impl;

import com.sei.tamias.TamiasApplication;
import com.sei.tamias.db.entity.Tag;
import com.sei.tamias.db.service.ITagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-02 23:52
 * @description :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TamiasApplication.class)
public class TagServiceTest2 {

    @Resource
    private ITagService tagService;

    @Test
    public void updateTest(){
        tagService.lambdaUpdate()
                .eq(Tag::getName, "tom")
                .set(Tag::getName, "mary");
    }

    @Test
    public void removeTest(){
        tagService.removeById(3);
    }

    @Test
    public void queryTest(){
        tagService.lambdaQuery()
                .eq(Tag::getName, "tom")
                .list().forEach(System.out::println);
    }

}
