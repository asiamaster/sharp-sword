package com.dili.ss.util;

import java.util.concurrent.ExecutorService;

public interface IExportThreadPoolExecutor {


	void init();

	void destory();

	ExecutorService getCustomThreadPoolExecutor();

}