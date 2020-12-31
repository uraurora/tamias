package com.sei.tamias.ext.task.config;

import com.sei.tamias.ext.task.enums.TaskTypeEnum;

public interface ITaskConfig {

    /**
     * 获取任务类型，具体见{@link TaskTypeEnum}
     * @return 任务类型
     */
    TaskTypeEnum getTaskType();



}
