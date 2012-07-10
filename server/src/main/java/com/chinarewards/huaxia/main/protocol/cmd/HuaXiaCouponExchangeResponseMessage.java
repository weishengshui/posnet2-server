package com.chinarewards.huaxia.main.protocol.cmd;

import java.util.Date;

import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;

/**
 * 
 * @author weishengshui
 * 
 */
public class HuaXiaCouponExchangeResponseMessage implements ICommand {

	public static final long HUA_XIA_COUPON_EXCHANGE_CMD_ID_RESPONSE = 302;

	public static final int RESULT_SUCCESS = 0;//成功
	public static final int RESULT_ALREADY_GET = 1;//已经领取
	public static final int RESULT_CDKEY_INVALID = 2;//验证信息无效
	public static final int RESULT_ERROR_OTHERS = 3;//非业务类型错误
	
	public static final String TITLE = "华夏银行";
	
	private long cmdId;
	// 应答结果
	private int result;
	// 交易时间
	private Date xact_time;
	// 小票上的标题
	private int titleLength;
	private String title;
	// 小票/屏幕的内容
	private int tipLength;
	private String tip;

	@Override
	public long getCmdId() {
		return this.cmdId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public Date getXact_time() {
		return xact_time;
	}

	public void setXact_time(Date xact_time) {
		this.xact_time = xact_time;
	}

	public int getTitleLength() {
		return titleLength;
	}

	public void setTitleLength(int titleLength) {
		this.titleLength = titleLength;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTipLength() {
		return tipLength;
	}

	public void setTipLength(int tipLength) {
		this.tipLength = tipLength;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public void setCmdId(long cmdId) {
		this.cmdId = cmdId;
	}

}
