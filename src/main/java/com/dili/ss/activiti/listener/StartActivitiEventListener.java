package com.dili.ss.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

@Component
public class StartActivitiEventListener implements ActivitiEventListener {
    @Override
    public void onEvent(ActivitiEvent event) {
        System.out.println(event.getType());
    }

    @Override
    public boolean isFailOnException() {
        System.out.println("isFailOnException");
        // onEvent方法中的逻辑并不重要，日志失败异常可以被忽略
        return false;
    }
}
