package com.chinarewards.posnet.ws.module;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new CommonModule());

		install(new QQAdidasModule());
	}

}
