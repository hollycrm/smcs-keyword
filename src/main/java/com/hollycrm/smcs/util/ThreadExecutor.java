package com.hollycrm.smcs.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadExecutor {
	private static ExecutorService es = Executors.newCachedThreadPool();
	public static void execute(Runnable thread) throws Exception {
		es.execute(thread);
	}
	
	public static <X> Future<X> submit(Callable<X> call){
		return es.submit(call);
	}
}
