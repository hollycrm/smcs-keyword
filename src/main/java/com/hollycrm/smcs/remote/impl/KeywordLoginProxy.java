package com.hollycrm.smcs.remote.impl;

import com.hollycrm.smcs.remote.AbstractRmiProxy;
import com.hollycrm.smcs.remote.IKeywordService;
import com.hollycrm.smcs.remote.ILoginProxy;
import com.hollycrm.smcs.remote.exception.RmiException;

public class KeywordLoginProxy extends AbstractRmiProxy<IKeywordService> implements ILoginProxy{

	public KeywordLoginProxy(String rmiURL) throws RmiException {
		super(rmiURL);
		validateStub();
	}

	@Override
	public void addReLogin(Long bloggerId) throws RmiException {
		try{
			validateStub();
			stub.addReLogin(bloggerId);	
		}catch(Exception e){
			error(e);
		}
		
	}

}
