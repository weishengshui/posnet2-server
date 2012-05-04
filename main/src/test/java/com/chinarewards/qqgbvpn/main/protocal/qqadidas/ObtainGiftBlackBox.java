package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import com.chinarewards.qqgbvpn.main.qqadidas.vo.Receipt;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ScreenDisplay;

public class ObtainGiftBlackBox {

	Socket socket;
	OutputStream os;
	InputStream is;
	String memberKey;
	Date lastSuccessfulOperationTime;
	long result;
	ScreenDisplay display;
	Receipt receipt;

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

	public ScreenDisplay getDisplay() {
		return display;
	}

	public void setDisplay(ScreenDisplay display) {
		this.display = display;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}

	public Date getLastSuccessfulOperationTime() {
		return lastSuccessfulOperationTime;
	}

	public void setLastSuccessfulOperationTime(Date lastSuccessfulOperationTime) {
		this.lastSuccessfulOperationTime = lastSuccessfulOperationTime;
	}

	public long getResult() {
		return result;
	}

	public void setResult(long result) {
		this.result = result;
	}
}
