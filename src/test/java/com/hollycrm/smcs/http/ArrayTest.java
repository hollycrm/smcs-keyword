package com.hollycrm.smcs.http;


public class ArrayTest {


	
	/**存放ReceiveMessage 数组**/
	private final Long[] array;
	
	/**指向下一个可以存放元素的地址**/
	private  int idx = 0;
	
	/**指向下一个可以读取元素的地址**/
	private  int idy = 0;
	
	private final int capacity;
	
	/**锁**/
	private final Object obj = new Object();
	
	public ArrayTest(){
		this(16);
	}
	
	public ArrayTest(int capacity){
		array = new Long[capacity];
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

	public void push(Long a) throws Exception{
		synchronized(obj){
				int tmp = idx+1;
				if(tmp == capacity){
					tmp = 0;
				}
				if(tmp == idy){
					obj.wait();
				}
				array[idx] = a;
				idx = tmp;
				obj.notifyAll();
		}
	}

	public Long pull() throws Exception {
		Long receiveMessage = null;
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
