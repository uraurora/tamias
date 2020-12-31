package com.sei.tamias.ext.task.manage;

import com.sei.tamias.ext.data.IIntermediateData;
import com.sei.tamias.ext.task.config.ITaskConfig;
import com.sei.tamias.ext.task.context.ITaskContext;
import com.sei.tamias.ext.task.handler.ITaskHandler;

import java.util.List;
import java.util.function.Consumer;

import static com.sei.tamias.util.Options.*;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-30 16:01
 * @description :
 */
public class Task<RESULT> extends AbstractTask<RESULT> implements ITask {

    private final Consumer<? extends ITaskContext> doOnSuccess;

    private final Consumer<? extends ITaskContext> doOnError;


    //<editor-fold desc="builder pattern">

    /**
     * 内部builder类构建
     * @param builder 内部构造类
     */
    public Task(TaskBuilder<RESULT> builder){
        this.taskConfig = builder.taskConfig;
        this.taskHandlers = builder.taskHandlers;
        this.doOnSuccess = builder.doOnSuccess;
        this.doOnError = builder.doOnError;
    }

    public static<RESULT> TaskBuilder<RESULT> builder(){
        return new TaskBuilder<>();
    }


    public static final class TaskBuilder<RESULT> {
        protected ITaskConfig taskConfig;
        protected List<ITaskHandler<RESULT>> taskHandlers = listOf();
        private Consumer<? extends ITaskContext> doOnSuccess;
        private Consumer<? extends ITaskContext> doOnError;

        private TaskBuilder() {
        }

        public TaskBuilder<RESULT> withDoOnSuccess(Consumer<? extends ITaskContext> doOnSuccess) {
            this.doOnSuccess = doOnSuccess;
            return this;
        }

        public TaskBuilder<RESULT> doOnError(Consumer<? extends ITaskContext> doOnError) {
            this.doOnError = doOnError;
            return this;
        }

        public TaskBuilder<RESULT> withTaskConfig(ITaskConfig taskConfig) {
            this.taskConfig = taskConfig;
            return this;
        }

        public TaskBuilder<RESULT> withTaskHandler(ITaskHandler<RESULT> taskHandler) {
            this.taskHandlers.add(taskHandler);
            return this;
        }

        @SafeVarargs
        public final TaskBuilder<RESULT> withTaskHandlers(ITaskHandler<RESULT>... taskHandlers) {
            this.taskHandlers.addAll(listOf(taskHandlers));
            return this;
        }

        public TaskBuilder<RESULT> withTaskHandlers(List<ITaskHandler<RESULT>> taskHandlers) {
            this.taskHandlers.addAll(taskHandlers);
            return this;
        }

        public Task<RESULT> build() {
            return new Task<>(this);
        }
    }
    //</editor-fold>

    @Override
    void doOnSuccess() {
        // todo:
        this.doOnSuccess.accept(null);
    }

    @Override
    void doOnStart() {

    }

    @Override
    void doOnError() {
        doOnError.accept(null);
    }
}
