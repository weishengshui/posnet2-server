package com.chinarewards.qqgbvpn.mgmtui.guice;

import com.chinarewards.qqgbvpn.mgmtui.search.SearchLogic;
import com.chinarewards.qqgbvpn.mgmtui.search.SearchLogicImple;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class SearchModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SearchLogic.class).to(SearchLogicImple.class).in(Singleton.class);

	}

}
