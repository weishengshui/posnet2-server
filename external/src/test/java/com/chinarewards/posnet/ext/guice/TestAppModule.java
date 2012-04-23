package com.chinarewards.posnet.ext.guice;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import com.chinarewards.qqgbvpn.common.SimpleDateTimeModule;
import com.chinarewards.qqgbvpn.logic.journal.DefaultJournalModule;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * @time 2012-3-5   下午04:24:38
 * @author Seek
 */
@Ignore
public class TestAppModule extends AbstractModule {
	
	private List<Module> list = new ArrayList<Module>();

	public TestAppModule(){
		
		/* ws service */
		list.add(new TestWSModule());
		
		list.add(new DefaultJournalModule());
		
		list.add(new SimpleDateTimeModule());
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		for(Module module:list){
			install(module);
		}
	}

	public List<Module> getList() {
		return list;
	}

	public void setList(List<Module> list) {
		this.list = list;
	}

}
