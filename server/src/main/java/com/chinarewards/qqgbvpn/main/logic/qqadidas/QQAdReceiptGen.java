package com.chinarewards.qqgbvpn.main.logic.qqadidas;

import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.Receipt;

/**
 * 
 * 
 * @author yanxin
 * 
 */
public interface QQAdReceiptGen {

	public Receipt generateGiftReceipt(GiftReceiptGenModel genModel);

	public Receipt generatePrivilegeReceipt(PrivilegeReceiptGenModel genModel);
}
