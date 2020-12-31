package com.sei.tamias.ext.task.handler;

import com.sei.tamias.ext.data.IIntermediateData;

public interface ITaskHandler<RESULT> {

    RESULT process();

}
