package com.dili.ss.quartz.domain;

import java.io.Serializable;

/**
 * 调度时传递的消息
 * Created by asiam on 2017/4/18 0018.
 */
public class ScheduleMessage implements Serializable {
    //记录当前调度器调度了多少次
    private Integer sheduelTimes;
    //trigger和jobDetail组
    private String jobGroup;
    //trigger和jobDetail名
    private String jobName;
    //业务数据
    private String jobData;

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getSheduelTimes() {
        return sheduelTimes;
    }

    public void setSheduelTimes(Integer sheduelTimes) {
        this.sheduelTimes = sheduelTimes;
    }

    public String getJobData() {
        return jobData;
    }

    public void setJobData(String jobData) {
        this.jobData = jobData;
    }

}
