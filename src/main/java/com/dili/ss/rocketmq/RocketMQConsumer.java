package com.dili.ss.rocketmq;

import com.dili.ss.rocketmq.exception.RocketMqException;

public interface RocketMQConsumer {
	void startListener()
			throws RocketMqException;

	void stopListener()
			throws RocketMqException;
}
