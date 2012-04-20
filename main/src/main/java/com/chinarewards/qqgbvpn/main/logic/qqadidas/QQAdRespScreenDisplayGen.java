package com.chinarewards.qqgbvpn.main.logic.qqadidas;

import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ScreenDisplay;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftScreenDisplayGenModel;

public interface QQAdRespScreenDisplayGen {

	public ScreenDisplay genGiftRespScreenDisplay(
			GiftScreenDisplayGenModel genModel);

	public ScreenDisplay genPrivilegeRespScreenDisplay(
			PrivilegeScreenDisplayGenModel genModel);
}
