package com.dili.ss.rocketmq;

import com.alibaba.rocketmq.common.message.MessageExt;

public interface RocketMQListener {
	String getTopic();

	String getTags();

	void operate(MessageExt paramMessageExt);
}

