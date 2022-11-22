package com.hw.flowabledemo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 自定义监听器。
 * 需要正在Flowable-UI审批人——》任务监听器——》创建事件——》事件类型，如：create。——》类。如：com.hw.flowabledemo.listener.MyTaskListener
 * 当对应的名为MyHoliday的流程创建时，触发MyTaskListener监听器执行相应的事件
 */
@Slf4j
public class MyTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("MyTaskListener触发了..."+delegateTask.getName());
        if ("MyHoliday".equals(delegateTask.getName()) && "create".equals(delegateTask.getEventName())){
            //满足触发条件，设置assignee
            delegateTask.setAssignee("张三");
        }else {
            delegateTask.setAssignee("小李");
        }
    }
}
