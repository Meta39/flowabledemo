package com.hw.flowabledemo.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class SendRejectionMail implements JavaDelegate {

    /**
     * xml里flowable:class指向的触发器
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        //触发执行的逻辑 按照我们在流程中的定义应该给被拒绝的员工发送通知的邮件
        log.info("拒绝！不允许请假！请继续努力工作！");
    }
}
