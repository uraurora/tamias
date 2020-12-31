package com.sei.tamias.ext.task.manage;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-30 17:36
 * @description :
 */
public class TaskFactory {

    public Task.TaskBuilder configTask(){
        return Task.builder();
    }

    public TaskGroup.TaskGroupBuilder configTaskGroup(){
        return TaskGroup.builder();
    }

}
