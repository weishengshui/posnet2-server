package com.chinarewards.qqgbvpn.main.qqadidas.vo;

/**
 * Receipt that is printed to customer.
 * 
 * @author yanxin
 * 
 */
public class Receipt {

	private String title;
	private String content;

	public Receipt(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
}
