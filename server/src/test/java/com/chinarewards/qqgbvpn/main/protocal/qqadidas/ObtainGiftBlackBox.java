package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObtainGiftBlackBox {

	Socket socket;
	OutputStream os;
	InputStream is;
	String memberKey;
	// Used to generate pos screen display
	Map<String, Date> lastSuccessfulOpTimeMap = new HashMap<String, Date>();
	long result;
	String tip;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}

	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}

	public Map<String, Date> getLastSuccessfulOpTimeMap() {
		return lastSuccessfulOpTimeMap;
	}

	public void setLastSuccessfulOpTimeMap(
			Map<String, Date> lastSuccessfulOpTimeMap) {
		this.lastSuccessfulOpTimeMap = lastSuccessfulOpTimeMap;
	}

	public long getResult() {
		return result;
	}

	public void setResult(long result) {
		this.result = result;
	}
}
