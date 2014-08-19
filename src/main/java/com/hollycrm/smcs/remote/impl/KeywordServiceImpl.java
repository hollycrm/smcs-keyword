package com.hollycrm.smcs.remote.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.hollycrm.smcs.http.httpclient.impl.PublicHttpClientContainer;
import com.hollycrm.smcs.remote.IKeywordService;

public class KeywordServiceImpl extends UnicastRemoteObject implements IKeywordService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9137869677771614595L;

	public KeywordServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public void addReLogin(Long bloggerId) throws Exception {
		PublicHttpClientContainer.addReLogin(bloggerId);
	}

}
