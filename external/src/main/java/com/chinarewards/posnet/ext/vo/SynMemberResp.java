package com.chinarewards.posnet.ext.vo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SynMemberResp {
	private int returncode = -1;

	public SynMemberResp() {
	}

	public SynMemberResp(int returncode) {
		this.returncode = returncode;
	}

	public int getReturncode() {
		return returncode;
	}

	public void setReturncode(int name) {
		this.returncode = name;
	}
}