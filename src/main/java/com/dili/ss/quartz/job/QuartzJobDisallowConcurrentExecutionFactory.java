package com.dili.ss.quartz.job;

import com.dili.ss.quartz.TaskUtils;
import com.dili.ss.quartz.domain.QuartzConstants;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.domain.ScheduleMessage;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  @Author: wangmi
 *  @Description: 串行job，若一个方法一次执行不完下次轮转时则等待改方法执行完后才执行下一次操作
 *  否则会在到时间后再启用新的线程执行
 */
@DisallowConcurrentExecution
public class QuartzJobDisallowConcurrentExecutionFactory implements Job {

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        ScheduleJob scheduleJob = (ScheduleJob) jobExecutionContext.getMergedJobDataMap().get(QuartzConstants.jobDataMapScheduleJobKey);
        ScheduleMessage scheduleMessage = new ScheduleMessage();
        scheduleMessage.setJobData(scheduleJob.getJobData());
        TaskUtils.invokeMethod(scheduleJob, scheduleMessage);
    }
}
