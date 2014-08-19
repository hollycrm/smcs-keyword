package com.hollycrm.smcs.receive.bean;

import java.util.concurrent.ArrayBlockingQueue;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.IReceiveMessageContainer;

public class ReceiveMessageBlockArray implements IReceiveMessageContainer{
	
	private final ArrayBlockingQueue<ReceiveMessage> queue;
	
	public ReceiveMessageBlockArray(){
		this(16);
	}
	
	public ReceiveMessageBlockArray(int size){
		queue = new ArrayBlockingQueue<ReceiveMessage>(size);
	}
	
	@Override
	public void push(ReceiveMessage receiveMessage) throws Exception {
		boolean isOffer = false;
		while(!isOffer){
			isOffer = queue.offer(receiveMessage);
			try{
				Thread.sleep(100L);
			}catch(Exception e){
				e.printStackTrace();
			}			
		}
		
	}

	@Override
	public ReceiveMessage pull() throws Exception {
		return queue.take();
	}
	
}
