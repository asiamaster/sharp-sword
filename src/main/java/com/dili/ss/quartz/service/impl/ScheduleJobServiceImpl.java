package com.dili.ss.quartz.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.quartz.dao.ScheduleJobMapper;
import com.dili.ss.quartz.domain.QuartzConstants;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.service.JobTaskService;
import com.dili.ss.quartz.service.ScheduleJobService;
import com.dili.ss.util.CronDateUtils;
import org.apache.commons.collections4.ListUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-24 09:32:32.
 */
@Service
@ConditionalOnProperty(name = "quartz.enabled")
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJob, Long> implements ScheduleJobService {

    @Autowired
    private JobTaskService jobTaskService;

    public ScheduleJobMapper getActualDao() {
        return (ScheduleJobMapper)getDao();
    }

	@Override
    public void addJob(ScheduleJob scheduleJob, boolean overwrite){
	    try {
		    try {
			    //解析成功的话就是个固定的时间，需要判断是否超时，超时就不添加调度任务，避免报错
			    Date date = CronDateUtils.getDate(scheduleJob.getCronExpression());
			    if(date.getTime() >= System.currentTimeMillis()+5000){
				    jobTaskService.addJob(scheduleJob, overwrite);
			    }
		    } catch (ParseException e) {
			    //解析出错就不解析，继续添加调度任务
			    jobTaskService.addJob(scheduleJob, overwrite);
		    }
	    } catch (SchedulerException e) {
		    LOGGER.error(e.getMessage());
		    throw new RuntimeException(e);
	    }
    }

    @Override
    public int insert(ScheduleJob scheduleJob) {
        try {
            jobTaskService.addJob(scheduleJob, true);
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.insert(scheduleJob);
    }

    @Override
    public int insertSelective(ScheduleJob scheduleJob) {
        try {
            jobTaskService.addJob(scheduleJob, true);
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.insertSelective(scheduleJob);
    }


    @Override
    public int batchInsert(List<ScheduleJob> list) {
        try {
            for(ScheduleJob job : list){
                jobTaskService.addJob(job, true);
            }
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.batchInsert(list);
    }

    @Override
    public int update(ScheduleJob condtion) {
        try {
            jobTaskService.updateJob(condtion);
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.update(condtion);
    }

    @Override
    public int updateSelective(ScheduleJob condtion) {
        try {
            jobTaskService.updateJob(condtion);
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.updateSelective(condtion);
    }

    @Override
    public int batchUpdateSelective(List<ScheduleJob> list) {
        try {
            for(ScheduleJob job : list){
                    jobTaskService.updateJob(job);
            }
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.batchUpdateSelective(list);
    }

    @Override
    public int delete(List<Long> ids) {
        try {
            for(Long id : ids) {
                jobTaskService.deleteJob(get(id));
            }
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.delete(ids);
    }

    @Override
    public int delete(Long id) {
        try {
            jobTaskService.deleteJob(get(id));
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        return super.delete(id);
    }

    @Override
    public void resumeJob(ScheduleJob scheduleJob){
        try {
            jobTaskService.resumeJob(scheduleJob);
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        //有id就不用查了,直接改状态为STATUS_RUNNING
        if(scheduleJob.getId() != null) {
            ScheduleJob job = DTOUtils.newDTO(ScheduleJob.class);
            job.setId(scheduleJob.getId());
            job.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
            updateSelective(job);
        }else{
            List<ScheduleJob> jobs = list(scheduleJob);
            if(ListUtils.emptyIfNull(jobs).isEmpty()){
                return;
            }
            ScheduleJob job = DTOUtils.newDTO(ScheduleJob.class);
            job.setId(jobs.get(0).getId());
            job.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
            updateSelective(job);
        }
    }

    @Override
    public void pauseJob(ScheduleJob scheduleJob){
        try {
            jobTaskService.pauseJob(scheduleJob);
        } catch (SchedulerException e) {
	        LOGGER.error(e.getMessage());
	        throw new RuntimeException(e);
        }
        //有id就不用查了,直接改状态为STATUS_RUNNING
        if(scheduleJob.getId() != null) {
            ScheduleJob job = DTOUtils.newDTO(ScheduleJob.class);
            job.setId(scheduleJob.getId());
            job.setJobStatus(QuartzConstants.JobStatus.PAUSED.getCode());
            updateSelective(job);
        }else{
            List<ScheduleJob> jobs = list(scheduleJob);
            if(ListUtils.emptyIfNull(jobs).isEmpty()){
                return;
            }
            ScheduleJob job = DTOUtils.newDTO(ScheduleJob.class);
            job.setId(jobs.get(0).getId());
            job.setJobStatus(QuartzConstants.JobStatus.PAUSED.getCode());
            updateSelective(job);
        }
    }

	@Override
	public void refresh(ScheduleJob scheduleJob) {
		try {
			jobTaskService.updateJob(scheduleJob);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void refresh(List<ScheduleJob> scheduleJobs) {
		for(ScheduleJob scheduleJob : scheduleJobs){
			try {
				jobTaskService.updateJob(scheduleJob);
			} catch (SchedulerException e) {
				LOGGER.error(e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}
}