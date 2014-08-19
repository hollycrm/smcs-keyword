package com.hollycrm.smcs.task;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.service.fetch.FragmentService;

public abstract class GrabFragmentDispatcher implements Runnable,IDispatcher {

	public static final String FRAGMENT_DISPATCHER_POOL = "fragment.dispatcher.pool";
	private final Logger logger = LoggerFactory.getLogger(GrabFragmentDispatcher.class);
	
	private final static Long SLEEP_TIME = Long.parseLong(AppConfig.get("fragment.dispatcher.sleep"));

	public static final String KEYWORD_FRAGMENT ="search";
	public static final String PRIVATE_FRAGMENT ="private";
	public static final String BOARD_FRAGMENT ="board";
	public static final String NOTE_FRAGMENT = "note";

	private final FragmentService fragmentService;

	private final ExecutorService executors;	
	
	
	
	private CountDownLatch latch;
	
	public GrabFragmentDispatcher() {

		fragmentService = ApplicationContextHolder.getBean(FragmentService.class);
		executors = Executors.newFixedThreadPool(Integer.parseInt(AppConfig.get(FRAGMENT_DISPATCHER_POOL)));
	}

	public void polling() {
		Thread thread = new Thread(this);
		thread.setName("fragment dispatcher thread");
		thread.setDaemon(true);
		thread.start();
		logger.info("fragment dispatcher thread is start ");
	}

	@Override
	public void run() {

		while (true) {
			try {
				List<Fragment> list = this.fragmentService.findValidFragment(getFragmentType());
				latch = new CountDownLatch(list.size());
				for (Fragment fragment : list) {
					executors.execute(buildWorker(fragment));					
				}
				latch.await();
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		}

	}
	
	public abstract AbsGrabMessageWorker buildWorker(Fragment fragment);
	

	protected abstract String getFragmentType();
	
	
	@Override
	public void release(Object obj) {
		latch.countDown();
	}

}
