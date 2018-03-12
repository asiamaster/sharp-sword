package com.dili.ss.quartz.service;


import com.dili.ss.base.BaseService;
import com.dili.ss.quartz.domain.ScheduleJob;
import org.quartz.SchedulerException;

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
	void addJob(ScheduleJob scheduleJob, boolean overwrite);

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

	/**
	 * 获取所有计划中的任务列表
	 *
	 * @return
	 * @throws SchedulerException
	 */
	List<ScheduleJob> getAllJob() throws SchedulerException;

	/**
	 * 获取正在运行中的ScheduleJob
	 *
	 * @return
	 * @throws SchedulerException
	 */
	List<ScheduleJob> getRunningJob() throws SchedulerException;

	/**
	 * 调度删除一个job
	 *
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	void deleteJob(ScheduleJob scheduleJob) throws SchedulerException;

	/**
	 * 修改
	 * @param job
	 * @param schedule 是否进行调度
	 * @return
	 */
	int update(ScheduleJob job, boolean schedule);

	/**
	 * 修改
	 * @param job
	 * @param schedule 是否进行调度
	 * @return
	 */
	int updateSelective(ScheduleJob job, boolean schedule);

	/**
	 * 批量修改
	 * @param list
	 * @param schedule 是否进行调度
	 */
	int batchUpdateSelective(List<ScheduleJob> list, boolean schedule);

	/**
	 * 删除
	 * @param ids
	 * @param schedule 是否进行调度
	 * @return
	 */
	int delete(List<Long> ids, boolean schedule);

	/**
	 * 删除
	 * @param id
	 * @param schedule 是否进行调度
	 * @return
	 */
	int delete(Long id, boolean schedule);

	int insert(ScheduleJob scheduleJob, boolean schedule);

	int insertSelective(ScheduleJob scheduleJob, boolean schedule);

	int batchInsert(List<ScheduleJob> list, boolean schedule);
}