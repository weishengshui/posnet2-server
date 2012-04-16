package com.chinarewards.qqgbvpn.mgmtui.struts.qq.adidas;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search.QQMemberSearchLogic;
import com.chinarewards.qqgbvpn.mgmtui.struts.BaseAction;

/**
 * This action is used to query qq activity member status which is provided to
 * qq customer.
 * 
 * @author weishengshui
 * 
 */
public class QQMemberSearchAction extends BaseAction {

	private static final long serialVersionUID = 6257809328181428130L;
	private String memberKey;
	private List<QQActivityHistory> historys;

	public List<QQActivityHistory> getHistorys() {
		return historys;
	}

	public void setHistorys(List<QQActivityHistory> historys) {
		this.historys = historys;
	}

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}
	
	/**
	 * check the member key is not blank
	 */
	@Override
	public void validate() {
		
	}

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	/**
	 * this method is used to find qq activity history by member key
	 * 
	 * @return
	 */
	public String searchMemberObtainStatus() {
		if (null == memberKey || "".equals(memberKey.trim())) {
			addFieldError("inputError", "memberKey为空！");
			log.debug("memberKey is empty");
		}
		QQMemberSearchLogic searchLogic = super.getInstance(QQMemberSearchLogic.class);
		setHistorys(searchLogic.findQqActivityHistoryByMemberKey(memberKey));
		if(this.getHistorys().size()==0){
			addFieldError("inputError", "没有数据可显示！");
			return INPUT;
		}
		log.debug("Success for QQ activity History ");
		return SUCCESS;
	}
}
