package com.sei.tamias.ext.task.manage;

import com.sei.tamias.ext.data.IIntermediateData;
import com.sei.tamias.ext.task.config.ITaskConfig;
import com.sei.tamias.ext.task.handler.ITaskHandler;

import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-30 16:45
 * @description :
 */
public abstract class AbstractTask<RESULT> {

    protected ITaskConfig taskConfig;

    protected List<ITaskHandler<RESULT>> taskHandlers;

    abstract void doOnSuccess();

    abstract void doOnStart();

    abstract void doOnError();

    /**
     * 触发任务执行
     */
    public void execute(){
        // 1.读取任务配置

        // 2.


    }

}
