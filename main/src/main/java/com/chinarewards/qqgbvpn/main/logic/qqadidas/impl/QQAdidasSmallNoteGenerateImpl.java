package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasSmallNoteGenerate;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrvilegePrintModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.SmallNote;

public class QQAdidasSmallNoteGenerateImpl implements QQAdidasSmallNoteGenerate {

	public static final String dateFormatStr = "yyyy-MM-dd HH:mm:ss";

	@Override
	public SmallNote generateAsObtainGift(String memberKey) {
		SmallNote note = new SmallNote();
		String title = "Adidas Neo 五月新品";
		StringBuffer content = new StringBuffer();
		content.append(memberKey).append("可以领取免费礼品一份.");
		note.setTitle(title);
		note.setContent(content.toString());
		return note;
	}

	@Override
	public SmallNote generateAsObtainPrivilege(
			ObtainPrvilegePrintModel printModel) {
		SmallNote note = new SmallNote();
		String title = "Adidas Neo 五月新品";
		StringBuffer content = new StringBuffer();
		if (printModel.isExistLastTimeConsume()) {
			DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
			content.append(printModel.getMemberKey())
					.append("在")
					.append(dateFormat.format(printModel.getLastConsumeDate()))
					.append("获得")
					.append((int) printModel.getLastRebateAmt())
					.append("元折扣优惠.本次消费")
					.append((int) printModel.getConsumeAmt())
					.append("元,享受")
					.append((int) printModel.getRebateAmt())
					.append("元折扣优惠.折扣后实际支付金额为")
					.append((int) (printModel.getConsumeAmt() - printModel
							.getRebateAmt())).append("元.");
		} else if (printModel.getRebateAmt() == QQAdidasConstant.REBATE_HALF_AMOUNT) {
			content.append(printModel.getMemberKey())
					.append("本次消费")
					.append((int) printModel.getConsumeAmt())
					.append("元.")
					.append("享受")
					.append((int) printModel.getRebateAmt())
					.append("元折扣优惠,折扣后实际支付金额为")
					.append((int) (printModel.getConsumeAmt() - printModel
							.getRebateAmt()))
					.append("元.")
					.append("还一次消费")
					.append((int) QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE)
					.append("-")
					.append((int) QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE)
					.append("元,折扣")
					.append((int) (QQAdidasConstant.REBATE_FULL_AMOUNT - QQAdidasConstant.REBATE_HALF_AMOUNT))
					.append("元的机会.");
		} else {
			content.append(printModel.getMemberKey())
					.append("本次消费")
					.append((int) printModel.getConsumeAmt())
					.append("元.享受")
					.append((int) printModel.getRebateAmt())
					.append("元折扣优惠,折扣后实际支付金额为")
					.append((int) (printModel.getConsumeAmt() - printModel
							.getRebateAmt())).append("元.");
		}
		note.setTitle(title);
		note.setContent(content.toString());
		return note;
	}
}
