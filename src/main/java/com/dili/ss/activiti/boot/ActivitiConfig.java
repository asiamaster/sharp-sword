package com.dili.ss.activiti.boot;

import com.dili.http.okhttp.utils.B;
import com.dili.ss.activiti.component.CustomProcessDiagramGenerator;
import com.dili.ss.activiti.consts.ActivitiConstants;
import com.dili.ss.activiti.listener.GlobalActivitiEventListener;
import com.dili.ss.activiti.util.ImageGenerator;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangmi
 * @date 2019-2-27 10:02:47
 * @since 1.0
 */
@Configuration
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    @Resource
    private GlobalActivitiEventListener globalActivitiEventListener;
    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        springProcessEngineConfiguration.setActivityFontName(ActivitiConstants.FONT_NAME);
        springProcessEngineConfiguration.setAnnotationFontName(ActivitiConstants.FONT_NAME);
        springProcessEngineConfiguration.setLabelFontName(ActivitiConstants.FONT_NAME);
        springProcessEngineConfiguration.setIdGenerator(new IdGen());
        try {
            ProcessDiagramGenerator processDiagramGenerator = (ProcessDiagramGenerator) ((Class<ProcessDiagramGenerator>)B.b.g("customProcessDiagramGenerator")).newInstance();
            springProcessEngineConfiguration.setProcessDiagramGenerator(processDiagramGenerator);
            ImageGenerator.diagramGenerator = (CustomProcessDiagramGenerator) processDiagramGenerator;
        } catch (Exception e) {
            return;
        }
        springProcessEngineConfiguration.setHistory(HistoryLevel.NONE.getKey());
        List<ActivitiEventListener> activitiEventListener=new ArrayList<ActivitiEventListener>();
        activitiEventListener.add(globalActivitiEventListener);//配置全局监听器
        springProcessEngineConfiguration.setEventListeners(activitiEventListener);
    }

}
