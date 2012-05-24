package com.chinarewards.qqgbvpn.testing.lab.business.message;

import java.util.Map;

import com.chinarewards.qqgbvpn.testing.exception.BuildBodyMessageException;

public interface BuildMessage {
	
	/**
	 * description：build a body message
	 * @param context
	 * @return
	 * @throws BuildBodyMessageException
	 * @time 2011-9-30   下午05:52:32
	 * @author Seek
	 */
	public byte[] buildBodyMessage(Map<String, String> context)
		throws BuildBodyMessageException;
	
}
