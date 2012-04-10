package com.chinarewards.qqgbvpn.mgmtui.search;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.mgmtui.struts.BaseAction;

public class SearchAction extends BaseAction {
	private static final long serialVersionUID = 6257809328181428130L;
	private String cdkey;
	private List<QQActivityHistory> historys;
	
	public List<QQActivityHistory> getHistory() {
		return historys;
	}
	public void setHistory(List<QQActivityHistory> history) {
		this.historys = history;
	}
	public String getCdkey() {
		return cdkey;
	}
	public void setCdkey(String cdkey) {
		this.cdkey = cdkey;
	}
	@Override
	public void validate() {
		if(null==cdkey || "".equals(cdkey.trim())){
			addFieldError("inputError", "cdkey为空");
		}
		log.debug("cdkey is empty");
	}
	
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	public void search(){
		SearchLogic searchLogic = super.getInstance(SearchLogic.class);
		setHistory(searchLogic.findQqActivityHistoryByCdkey(cdkey));		
	}
	
	
	
	
	

}
