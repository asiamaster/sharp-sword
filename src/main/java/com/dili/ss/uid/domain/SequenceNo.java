package com.dili.ss.uid.domain;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by asiamaster on 2017/12/8 0008.
 */
public class SequenceNo {
	private Long step = 50L;//步长
	private AtomicLong startSeq = new AtomicLong(1);//开始ID
	private Long finishSeq = 0L;//结束ID
	public synchronized Long next(){
		return startSeq.getAndIncrement();
	}
	//增长指定数值
	public synchronized Long next(int increment){
		return startSeq.getAndAccumulate(increment, (a, b) -> a + b);
	}

	public Long getStep() {
		return step;
	}
	public void setStep(Long step){
		this.step = step;
	}
	public Long getStartSeq() {
		return startSeq.get();
	}

	public void setStartSeq(Long startSeq) {
		this.startSeq = new AtomicLong(startSeq);
	}

	public Long getFinishSeq() {
		return finishSeq;
	}

	public void setFinishSeq(Long finishSeq) {
		this.finishSeq = finishSeq;
	}
}
