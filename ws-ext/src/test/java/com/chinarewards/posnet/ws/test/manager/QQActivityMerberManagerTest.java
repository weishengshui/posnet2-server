package com.chinarewards.posnet.ws.test.manager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;

import com.chinarewards.posnet.ws.guice.TestAppModule;
import com.chinarewards.posnet.ws.manager.QQActivityMerberManager;
import com.chinarewards.posnet.ws.test.JpaGuiceTest;
import com.chinarewards.posnet.ws.util.DESECBUtil;
import com.chinarewards.posnet.ws.util.json.JsonUtil;
import com.chinarewards.posnet.ws.vo.SynMemberReq;
import com.chinarewards.posnet.ws.vo.SynMemberResp;
import com.chinarewards.qqgbpvn.core.test.CommonTestConfigModule;
import com.chinarewards.qqgbvpn.core.jpa.JpaPersistModuleBuilder;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * description：QQVIP-Adidas
 * @copyright binfen.cc
 * @projectName ws-ext
 * @time 2012-4-23   上午10:23:34
 * @author Seek
 */
public class QQActivityMerberManagerTest extends JpaGuiceTest {
	
	private static final String QQ_VIP_ADIDAS_DES_SECRET_KEY  = "qq.vip.adidas.des.secretkey";
	
	private static final int SUCCESS = 0;
	private static final int PARSE_ERR = 1;
	private static final int PARAM_LACK = 2;
	private static final int MEMBER_KEY_REPEAT = 3;
	private static final int SYS_ERR = 4;
	
	
	@Override
	protected Module[] getModules() {
		CommonTestConfigModule confModule = new CommonTestConfigModule();
		Configuration configuration = confModule.getConfiguration();
		
		addConfiguration(configuration);

		JpaPersistModule jpaModule = new JpaPersistModule("posnet");
		JpaPersistModuleBuilder builder = new JpaPersistModuleBuilder();
		builder.configModule(jpaModule, configuration, "db");
		
		TestAppModule appModules = new TestAppModule();
//		appModules.getList().add(createTestConfigModule());
		
		return new Module[] { appModules, jpaModule, confModule };
	}

	private void addConfiguration(Configuration conf) {
		// URL for QQ meishi
		conf.setProperty(QQ_VIP_ADIDAS_DES_SECRET_KEY,
				"qqadidas");
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see com.chinarewards.qqgbpvn.main.test.JpaGuiceTest#tearDown()
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	
	/******************************** Test Case *******************************/
	@Test
	public void testNormalConditions() throws Exception {
		SynMemberResp synMemberResp = null;
		String jsonStr = null;
		byte[] bytes = null;
			
		Configuration configuration = getInjector().getInstance(
				Configuration.class);
		final String secretKey = configuration.getString(QQ_VIP_ADIDAS_DES_SECRET_KEY);
		
		QQActivityMerberManager qqActivityMerberManager = getInjector().getInstance(
				QQActivityMerberManager.class);
		
		
		final String memberKey = "158111122220001";
		final String timestamp = "20120412T234059+0800";
		
		SynMemberReq synMemberReq1 = new SynMemberReq();
		synMemberReq1.setMemberKey(memberKey);
		synMemberReq1.setTimestamp(timestamp);
		
		jsonStr = JsonUtil.formatObject(synMemberReq1);
		bytes = Base64.encodeBase64(DESECBUtil.encrypt(jsonStr.getBytes(), secretKey));
		synMemberResp = qqActivityMerberManager.generateQQActivityMember(new String(bytes), secretKey);
		
		System.out.println("synMemberResp:"+synMemberResp);
		Assert.assertEquals(SUCCESS, synMemberResp.getReturncode());
	}
	
	@Test
	public void testDataParse() throws Exception {
		SynMemberResp synMemberResp = null;
		String jsonStr = null;
		byte[] bytes = null;
			
		Configuration configuration = getInjector().getInstance(
				Configuration.class);
		final String secretKey = configuration.getString(QQ_VIP_ADIDAS_DES_SECRET_KEY);
		
		QQActivityMerberManager qqActivityMerberManager = getInjector().getInstance(
				QQActivityMerberManager.class);
		
		
		jsonStr = "{xxx}";
		bytes = Base64.encodeBase64(DESECBUtil.encrypt(jsonStr.getBytes(), secretKey));
		synMemberResp = qqActivityMerberManager.generateQQActivityMember(new String(bytes), secretKey);
		
		System.out.println("synMemberResp:"+synMemberResp);
		Assert.assertEquals(PARSE_ERR, synMemberResp.getReturncode());
	}
	
	@Test
	public void testParameterLack() throws Exception {
		SynMemberResp synMemberResp = null;
		String jsonStr = null;
		byte[] bytes = null;
			
		Configuration configuration = getInjector().getInstance(
				Configuration.class);
		final String secretKey = configuration.getString(QQ_VIP_ADIDAS_DES_SECRET_KEY);
		
		QQActivityMerberManager qqActivityMerberManager = getInjector().getInstance(
				QQActivityMerberManager.class);
		
		
		final String timestamp = "20120412T234059+0800";
		
		SynMemberReq synMemberReq1 = new SynMemberReq();
		synMemberReq1.setMemberKey(null);
		synMemberReq1.setTimestamp(timestamp);
		
		jsonStr = JsonUtil.formatObject(synMemberReq1);
		bytes = Base64.encodeBase64(DESECBUtil.encrypt(jsonStr.getBytes(), secretKey));
		synMemberResp = qqActivityMerberManager.generateQQActivityMember(new String(bytes), secretKey);
		
		System.out.println("synMemberResp:"+synMemberResp);
		Assert.assertEquals(PARAM_LACK, synMemberResp.getReturncode());
	}
	
	@Test
	public void testMemberKeyRepeat() throws Exception {
		SynMemberResp synMemberResp = null;
		String jsonStr = null;
		byte[] bytes = null;
			
		Configuration configuration = getInjector().getInstance(
				Configuration.class);
		final String secretKey = configuration.getString(QQ_VIP_ADIDAS_DES_SECRET_KEY);
		
		QQActivityMerberManager qqActivityMerberManager = getInjector().getInstance(
				QQActivityMerberManager.class);
		
		
		final String memberKey = "158111122220002";
		final String timestamp = "20120412T234059+0800";
		
		SynMemberReq synMemberReq1 = new SynMemberReq();
		synMemberReq1.setMemberKey(memberKey);
		synMemberReq1.setTimestamp(timestamp);
		
		jsonStr = JsonUtil.formatObject(synMemberReq1);
		bytes = Base64.encodeBase64(DESECBUtil.encrypt(jsonStr.getBytes(), secretKey));
		synMemberResp = qqActivityMerberManager.generateQQActivityMember(new String(bytes), secretKey);
		
		System.out.println("synMemberResp:"+synMemberResp);
		Assert.assertEquals(SUCCESS, synMemberResp.getReturncode());
		
		
		SynMemberReq synMemberReq2 = new SynMemberReq();
		synMemberReq2.setMemberKey(memberKey);
		synMemberReq2.setTimestamp(timestamp);
		
		jsonStr = JsonUtil.formatObject(synMemberReq2);
		bytes = Base64.encodeBase64(DESECBUtil.encrypt(jsonStr.getBytes(), secretKey));
		synMemberResp = qqActivityMerberManager.generateQQActivityMember(new String(bytes), secretKey);
		
		System.out.println("synMemberResp:"+synMemberResp);
		Assert.assertEquals(MEMBER_KEY_REPEAT, synMemberResp.getReturncode());
	}
	/******************************** Test Case *******************************/
	
}
