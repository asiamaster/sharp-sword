package com.dili.ss.quartz.domain;

import com.dili.ss.domain.BaseDomain;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: wangmi
*  @Description: 计划任务信息
 */
@Table(name = "schedule_job")
public class ScheduleJob extends BaseDomain {

	public static final Integer STATUS_RUNNING = 1;
	public static final Integer STATUS_NOT_RUNNING = 0;
	public static final Integer CONCURRENT_IS = 1;
	public static final Integer CONCURRENT_NOT = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Date created;

	private Date modified;

	@Column(name = "job_name")
	private String jobName;

	@Column(name = "job_group")
	private String jobGroup;

	/**
	 * 是否启动任务
	 */
	@Column(name = "job_status")
	private Integer jobStatus;

	/**
	 * 任务数据(JSON)
	 */
	@Column(name = "job_data")
	private String jobData;

	@Column(name = "cron_expression")
	private String cronExpression;

	@Column(name = "repeat_interval")
	private Integer repeatInterval;

	@Column(name = "start_delay")
	private Integer startDelay;

	private String description;

	/**
	 * 任务执行时调用哪个类的方法 包名+类名
	 */
	@Column(name = "bean_class")
	private String beanClass;

	/**
	 * 以后会支持远程调用restful url
	 */
	private String url;

	@Column(name = "is_concurrent")
	private Integer isConcurrent;

	@Column(name = "spring_id")
	private String springId;

	@Column(name = "method_name")
	private String methodName;

	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return modified
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * @param modified
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * @return job_name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return job_group
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * @param jobGroup
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * 获取是否启动任务
	 *
	 * @return job_status - 是否启动任务
	 */
	public Integer getJobStatus() {
		return jobStatus;
	}

	/**
	 * 设置是否启动任务
	 *
	 * @param jobStatus 是否启动任务
	 */
	public void setJobStatus(Integer jobStatus) {
		this.jobStatus = jobStatus;
	}

	/**
	 * 获取任务数据(JSON)
	 *
	 * @return job_data - 任务数据(JSON)
	 */
	public String getJobData() {
		return jobData;
	}

	/**
	 * 设置任务数据(JSON)
	 *
	 * @param jobData 任务数据(JSON)
	 */
	public void setJobData(String jobData) {
		this.jobData = jobData;
	}

	/**
	 * @return cron_expression
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * @param cronExpression
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * @return repeat_interval
	 */
	public Integer getRepeatInterval() {
		return repeatInterval;
	}

	/**
	 * @param repeatInterval
	 */
	public void setRepeatInterval(Integer repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	/**
	 * @return start_delay
	 */
	public Integer getStartDelay() {
		return startDelay;
	}

	/**
	 * @param startDelay
	 */
	public void setStartDelay(Integer startDelay) {
		this.startDelay = startDelay;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取任务执行时调用哪个类的方法 包名+类名
	 *
	 * @return bean_class - 任务执行时调用哪个类的方法 包名+类名
	 */
	public String getBeanClass() {
		return beanClass;
	}

	/**
	 * 设置任务执行时调用哪个类的方法 包名+类名
	 *
	 * @param beanClass 任务执行时调用哪个类的方法 包名+类名
	 */
	public void setBeanClass(String beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * 获取以后会支持远程调用restful url
	 *
	 * @return url - 以后会支持远程调用restful url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置以后会支持远程调用restful url
	 *
	 * @param url 以后会支持远程调用restful url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return is_concurrent
	 */
	public Integer getIsConcurrent() {
		return isConcurrent;
	}

	/**
	 * @param isConcurrent
	 */
	public void setIsConcurrent(Integer isConcurrent) {
		this.isConcurrent = isConcurrent;
	}

	/**
	 * @return spring_id
	 */
	public String getSpringId() {
		return springId;
	}

	/**
	 * @param springId
	 */
	public void setSpringId(String springId) {
		this.springId = springId;
	}

	/**
	 * @return method_name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}