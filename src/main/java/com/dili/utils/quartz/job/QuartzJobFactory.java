package com.dili.utils.quartz.job;

import com.dili.utils.quartz.TaskUtils;
import com.dili.utils.quartz.domain.QuartzConstants;
import com.dili.utils.quartz.domain.ScheduleJob;
import com.dili.utils.quartz.domain.ScheduleMessage;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wangmi
 * @Description: 并行job
 */
public class QuartzJobFactory implements Job {

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        ScheduleJob scheduleJob = (ScheduleJob) jobExecutionContext.getMergedJobDataMap().get(QuartzConstants.jobDataMapScheduleJobKey);
        ScheduleMessage scheduleMessage = new ScheduleMessage();
        scheduleMessage.setJobData(scheduleJob.getJobData());
        TaskUtils.invokeMethod(scheduleJob, scheduleMessage);

    }
}
