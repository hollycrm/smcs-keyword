package com.hollycrm.smcs.assist;

import java.util.concurrent.locks.ReentrantLock;

public abstract class Avaliable {
	
	public static final long ONE_SECOND = 1000L;
	
	public static final long HALF_OF_MINUTE = 30000l;
	
	public static final long ONE_MINUTE = HALF_OF_MINUTE<<1;
	
	public static final long TWO_MINUTE = ONE_MINUTE<<1;
	
	public static final int LEAST_COUNT = 5;
	
	public static final long ONE_HOUR = ONE_MINUTE*60;
	
	protected long sleepTime = 0;
	
	/**运行次数**/
	protected int times = 0;
	
	protected long runningTime;
	
	protected  long interval;
	
	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	private final ReentrantLock runLock = new ReentrantLock(); 
	
	
	public Avaliable(long interval){
		this.interval = interval;
	}
	
	public synchronized void setTimes(int times) {
		if(times >= Integer.MAX_VALUE){
			times = 0;
		}
		this.times = times;
	}
	
	public synchronized void addTimes(){
		this.times++;
	}
	
	public boolean atRightTimes(){
		if((times%6) == 0){
			return true;
		}
		return false;
	}

	
	public void sleep(){
		setSleepTime(getSleepTime()+interval);
	}
	
	
	public boolean isRunning(){
		if(getSleepTime() <= 0){
			return true;
		}		
		return false;
	}

	public void adjustSleepTime(long time){
		long runtime = getSleepTime();
		runtime -= time;
		
		setSleepTime(runtime < 0 ? 0 : runtime);
	}
	
	public void countRuntime(int count){
		double rate = (1 - ((count*1.0)/leaseCount())) * 0.5;
		long runtime = (long) ((interval * rate) + getSleepTime());
		if(runtime<0){
			runtime = 0;
		}
		setSleepTime(runtime);
		
	}
	
	private long getSleepTime() {
		runLock.lock();
		try{
			return sleepTime;
		}finally{
			runLock.unlock();
		}
		
	}

	private void setSleepTime(long sleeptime) {
		runLock.lock();
		try{
			if(sleeptime > maxRuntime()){
				sleeptime = maxRuntime();
			}
			this.sleepTime = sleeptime;
		}finally{
			runLock.unlock();
		}
		
	}
	
	/**
	 * 字类根据需要覆盖该方法
	 * @return 最大休息时间
	 */
	protected  long maxRuntime(){
		return interval<<1;
	}


	
	protected abstract int leaseCount();
	

	public long getRunnginTime() {
		return runningTime;
	}

	public void setRunnginTime(long runningTime) {
		this.runningTime = runningTime;
	}
	
	public boolean isOverNormalRunntime(){
		if(runningTime == 0){
			return false;
		}
		return (System.currentTimeMillis() - runningTime) > ONE_HOUR;
	}
}
