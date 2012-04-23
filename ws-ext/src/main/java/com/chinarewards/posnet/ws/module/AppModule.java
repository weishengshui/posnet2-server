package com.chinarewards.posnet.ws.module;

import com.chinarewards.qqgbvpn.logic.journal.DefaultJournalModule;
import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new CommonModule());
		
		install(new DefaultJournalModule());

		install(new QQAdidasModule());
	}

}
