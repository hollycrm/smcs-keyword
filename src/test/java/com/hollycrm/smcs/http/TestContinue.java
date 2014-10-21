package com.hollycrm.smcs.http;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.IReceiveMessageContainer;
import com.hollycrm.smcs.receive.bean.ExitMessage;
import com.hollycrm.smcs.receive.bean.ReceiveMessageBlockArray;


public class TestContinue {

	@Test
	public void testContinue(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
		Date now = new Date();
		Date halfOfHourBefore = DateUtils.addHours(now, -12);
		System.out.println(sdf.format(halfOfHourBefore));
	}
	
	@Test
	public void stringAppend(){
		
		try{
			tttt();
		}finally{
			System.out.println("finally");
		}
	}
	
	private void tttt(){
		throw new IllegalArgumentException();
	}
	
	
	@Test
	public void testFuture(){
		ExecutorService es = Executors.newCachedThreadPool();
		Callable<Long> call = new Callable<Long>(){

			@Override
			public Long call() throws Exception {
				Thread.sleep(10000);
				return 1L;
			}
			
		};
		
		Future<Long> future = es.submit(call);
		try {
			Long l = future.get(1000L, TimeUnit.MILLISECONDS);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void test23(){
		final IReceiveMessageContainer array = new ReceiveMessageBlockArray();
		Thread thread1 = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					for(int i=0; i<100;i++){
						
						
						if(i == 10){
							array.push(new ExitMessage());
						}else{
							array.push(new com.hollycrm.smcs.entity.receive.EventMessage());
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		Thread thread2 = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					while(true){
						ReceiveMessage receiveMessage = array.pull();
						if(receiveMessage instanceof ExitMessage){
							break;
						}
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		thread1.start();
		thread2.start();
		System.out.println(2222);
	}
	
	@Test
	public void testMap(){
		Map<Long, Long> map = new ConcurrentHashMap<Long, Long>();
		
		map.put(1L, 1L);
		map.put(2L, 2L);
		map.put(3L, 3L);
		Set<Long> set = map.keySet();
		for(Long udi:set){
			System.out.println(map.get(udi));
			if(udi == 2L){
				map.remove(udi);
			}
		}
		System.out.println(map);
		
	}
	
}
