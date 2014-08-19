package com.hollycrm.smcs.receive;


/**
 * 接收消息接口
 * @author dingqj
 *
 */
public interface IReceiveMessageStrategy {
	
	/**
	 *  1、为确保应用及V用户信息安全，此接口必须在服务器端调用；
		*	2、调用接口的登录帐号为该appkey的所有者，需要使用所有者帐号通过Base Auth的方式；
		*	3、如appkey已绑定IP地址，调用接口的请求IP须为绑定的IP；
		*	4、指定的uid用户为蓝V；
		*	5、指定的uid用户已设置成将自己的微博私信、留言等消息箱服务交给当前应用托管;
		*	6、指定的uid用户已开启推送服务（当前托管即自动开启）;
		*	6、每条完整的新消息数据以json形式返回，默认采用UTF-8编码，且以\r\n分隔；
		*	7、新消息来源用户为蓝V且已开启消息服务时，新消息不推送；
		*	8、非常重要：为缓解服务压力，请求建立后约每5分钟会自动断开（如未自动断开请用程序断开），应用需兼容根据最后一次获取的新消息ID重新调此接口连接。
	 * @param sinceId 上次连接断开时的消息ID。保存断开后5分钟内的新消息，可以通过since_id获取断开五分钟内的新消息。
	 * 
	 * @throws Exception
	 */
	void receive(Long sinceId);
	
	/**
	 * 停止连接，退出
	 * @param uid
	 */
	void exit();

}
