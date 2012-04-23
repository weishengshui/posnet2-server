package com.chinarewards.posnet.ext.vo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SynMemberResp {
	
	public static final int SUCCESS = 0;
	public static final int PARSE_ERR = 1;
	public static final int PARAM_LACK = 2;
	public static final int MEMBER_KEY_REPEAT = 3;
	public static final int SYS_ERR = 4;
	
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