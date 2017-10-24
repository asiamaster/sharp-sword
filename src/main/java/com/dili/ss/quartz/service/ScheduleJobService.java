package com.dili.ss.quartz.service;


import com.dili.ss.base.BaseService;
import com.dili.ss.quartz.domain.ScheduleJob;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-24 09:32:32.
 */
public interface ScheduleJobService extends BaseService<ScheduleJob, Long> {

	/**
	 * 添加任务，不作数据处理
	 * @param scheduleJob 任务属性
	 * @param overwrite 是否覆盖原数据
	 */
	public void addJob(ScheduleJob scheduleJob, boolean overwrite);

	/**
	 * 恢复暂停的任务(会更新任务状态),至少需要JobName和GroupName,有id效率更高
	 * @param scheduleJob
	 */
	void resumeJob(ScheduleJob scheduleJob);

	/**
	 * 暂停任务(会更新任务状态),至少需要JobName和GroupName,有id效率更高
	 * @param scheduleJob
	 */
	void pauseJob(ScheduleJob scheduleJob);

	/**
	 * 刷新任务(不更新数据),至少需要(CronExpression或RepeatInterval)、JobName和GroupName,
	 * @param scheduleJob
	 */
	void refresh(ScheduleJob scheduleJob);

	/**
	 * 刷新任务(不更新数据),至少需要(CronExpression或RepeatInterval)、JobName和GroupName,
	 * @param scheduleJobs
	 */
	void refresh(List<ScheduleJob> scheduleJobs);
}