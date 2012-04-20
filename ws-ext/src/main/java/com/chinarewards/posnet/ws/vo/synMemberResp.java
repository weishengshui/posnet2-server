package com.chinarewards.posnet.ws.vo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class synMemberResp {
	private int returncode = -1;

	public synMemberResp() {
	}

	public synMemberResp(int returncode) {
		this.returncode = returncode;
	}

	public int getReturncode() {
		return returncode;
	}

	public void setReturncode(int name) {
		this.returncode = name;
	}
}