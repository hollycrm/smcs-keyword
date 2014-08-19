package com.hollycrm.smcs.receive.bean;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.IReceiveMessageContainer;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-14 上午10:21:20
 */
public class ReceiveMessageArray implements IReceiveMessageContainer{
	
	
	
	/**存放ReceiveMessage 数组**/
	private final ReceiveMessage[] array;
	
	/**指向下一个可以存放元素的地址**/
	private  int idx = 0;
	
	/**指向下一个可以读取元素的地址**/
	private  int idy = 0;
	
	private final int capacity;
	
	/**锁**/
	private final Object obj = new Object();
	
	public ReceiveMessageArray(){
		this(16);
	}
	
	public ReceiveMessageArray(int capacity){
		array = new ReceiveMessage[capacity];
		this.capacity = capacity;
	}
	
	/**
	 * 返回容器大小
	 * @return
	 */
	public int capacity() {
		return capacity;
	}

	/**
	 * 返回容器存放元素个数
	 * @return
	 */
	public int size() {		
			return java.lang.Math.abs(idy - idx);
		
	}

	@Override
	public void push(ReceiveMessage receiveMessage) throws Exception{
		synchronized(obj){
				int tmp = idx+1;
				if(tmp == capacity){
					tmp = 0;
				}
				if(tmp == idy){
					obj.wait();
				}
				array[idx] = receiveMessage;
				idx = tmp;
				obj.notifyAll();
		}
	}

	@Override
	public ReceiveMessage pull() throws Exception {
		ReceiveMessage receiveMessage = null;
		synchronized(obj){
			while(true){
				if(idx == idy){
					obj.wait();
					continue;
				}else{
					receiveMessage = array[idy];
					array[idy] = null;
					if (++idy == capacity()) {
						idy = 0;
					}
					obj.notifyAll();
					return receiveMessage;
				}
			}
			
		}
	}

}
