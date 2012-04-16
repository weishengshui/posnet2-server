package com.chinarewards.qqgbvpn.main.logic.qqadidas;

import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrvilegePrintModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.SmallNote;

public interface QQAdidasSmallNoteGenerate {

	public SmallNote generateAsObtainGift(String memberKey);

	public SmallNote generateAsObtainPrivilege(
			ObtainPrvilegePrintModel printModel);
}
