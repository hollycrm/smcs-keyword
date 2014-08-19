package com.hollycrm.smcs.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.config.AppConfig;

public abstract class AbsDispatcher<T extends Avaliable,Z extends Object> implements Runnable,IDispatcher{
	

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**线程池**/
	private   ExecutorService executors;
	
	/**待处理容器**/
	protected final  Map<Long, T> waiting;
	
	/**正在运行线程存放池**/
	protected   Set<Z> running;
	
	/**用来控制线程**/
	protected volatile boolean allowRunning = true; 
	
	/**线程池大小**/
	private static final int POOL_SIZE = Integer.parseInt(AppConfig.get("dispatcher.pool"));
	
	protected volatile int count = 0;
	
	protected static final long ONE_SECOND = 1000l;
	
	protected static final long HALF_OF_MINUTE = ONE_SECOND*30;
	
	protected static final long ONE_MINUTE = ONE_SECOND*60;
	
	protected static final long TWO_MINUTE = ONE_SECOND*120;
	

	
	public AbsDispatcher(){
		executors = Executors.newFixedThreadPool(POOL_SIZE);
		waiting = new ConcurrentHashMap<Long, T>();
		running = Collections.synchronizedSet(new HashSet<Z>());
		
	}
	
	/**
	 * 启动线程方法
	 */
	public void polling() {
		Thread thread = new Thread(this);
		thread.setName(getName()+" dispatcher ");
		thread.setDaemon(true);
		thread.start();		
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract String getName();
	
	@Override
	public void run() {
		while (true) {
			int dispatcherCount =0;
			while(allowRunning){
				try {
					Iterator<Entry<Long, T>> it = waiting.entrySet().iterator();
					while (it.hasNext()) {
						if(handleRunning(it.next().getValue())){
							dispatcherCount++;
						}
					}
					logger.info("启动"+dispatcherCount+"个"+getName()+"线程");
					Thread.sleep(getSleepTime());
				} catch (Exception e) {
					logger.info(e.getLocalizedMessage(), e);
				}
				dispatcherCount = 0;
			}
			
			
		}
	}

	/**
	 * 判断等待队列 中是否包含 
	 * @param id key
	 * @return
	 */
	private boolean waitingContains(Long id) {
		return waiting.containsKey(id);
	}
	
	/**
	 * 运行队列中是否包含
	 * @param t 
	 * @return
	 */
	private boolean runningContains(T t){
		if(running.contains(getRunningKey(t))){
			if(t.isOverNormalRunntime()){
				running.remove(getRunningKey(t));
				return false;
			}
			return true;
		}
		return false;
	}
	
	private void putWaiting(T t){
		waiting.put(getWaingKey(t), t);
	}
	
	/**
	 * 获取放到map key
	 * @param t
	 * @return
	 */
	protected abstract Long getWaingKey(T t);
	
	protected abstract Z getRunningKey(T t);
	
	protected void addRunning(T t){
		running.add(getRunningKey(t));
		t.setRunnginTime(System.currentTimeMillis());
		logger.info(getRunningKey(t).toString()+getName()+"正在运行");
	}

	/**
	 * 往等待队列appent t 
	 * @param t
	 */
	public void append(T t) {
		if (waitingContains(getWaingKey(t))) {
			handleAvaliable(t);
		} else {
			putWaiting(t);
			handleRunning(t);
		}
	}

	/**
	 * 从等待队列中移除
	 * @param id
	 */
	public void removeInvalidWaiting(Long id) {
		if (waitingContains(id)) {
			remove(id);
			logger.info("从等待队列中移除一个"+getName()+"key:"+id);
		}
	}

	protected abstract void handleAvaliable(T t); 
		

	/**
	 * 
	 * @param t
	 */
	public boolean handleRunning(T t) {
		if (!runningContains(t) && allowRunning) {
			t.adjustSleepTime(getSleepTime());
			if(t.isRunning()){
				addRunning(t);
				executors.execute(getWorder(t));
				return true;
			}
			
		}
		return false;

	}
	
	//protected abstract boolean isRunning(T t);

	
	protected abstract Runnable getWorder(T t);

	
	private void remove(Long id) {
		waiting.remove(id);		
	}
	

	/**
	 * 移除运行完的线程id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void release(Object obj) {		
		Z z= (Z) obj;
		running.remove(z);		
		logger.info(getName()+"key:"+z+"从运行队列中移除");
	}
	
	/**
	 * 线程休息时间
	 * @return
	 */
	protected abstract long getSleepTime();
	
	public void pause() {
		allowRunning = false;
	}

	
	public int getRunningCount() {
		return running.size();
	}

	
	public Set<Z> getRunning() {
		return running;
	}

	
	public void resetRunning() {
	 running = Collections.synchronizedSet(new HashSet<Z>());
	}

	
	public  boolean isRunning() {
		return allowRunning;
	}

	
	public void restartExecutor() {
		if(!executors.isShutdown()){
			executors.shutdown();
		}
		executors = Executors.newFixedThreadPool(POOL_SIZE);
	}
	
}
