//package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.Date;
//
//import org.apache.commons.configuration.BaseConfiguration;
//import org.apache.commons.configuration.Configuration;
//import org.junit.Test;
//
//import com.chinarewards.qq.adidas.domain.QQActivityHistory;
//import com.chinarewards.qqgbpvn.main.CommonTestConfigModule;
//import com.chinarewards.qqgbpvn.main.TestConfigModule;
//import com.chinarewards.qqgbpvn.main.test.JpaGuiceTest;
//import com.chinarewards.qqgbvpn.core.jpa.JpaPersistModuleBuilder;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateObtainGiftException;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;
//import com.chinarewards.qqgbvpn.main.guice.AppModule;
//import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
//import com.chinarewards.ws.ext.api.qq.adidas.exception.MemberKeyExistedException;
//import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
//import com.google.inject.Module;
//import com.google.inject.persist.jpa.JpaPersistModule;
//
///**
// * 
// * @author yanxin
// * 
// */
//
//public class QQAdidasActivityManagerImplTest extends JpaGuiceTest {
//
//	protected Module[] getModules() {
//
//		CommonTestConfigModule confModule = new CommonTestConfigModule();
//		Configuration configuration = confModule.getConfiguration();
//
//		JpaPersistModule jpaModule = new JpaPersistModule("posnet");
//		JpaPersistModuleBuilder builder = new JpaPersistModuleBuilder();
//		builder.configModule(jpaModule, configuration, "db");
//
//		return new Module[] { new AppModule(), new QQAdidasApiModule(),
//				jpaModule, confModule };
//	}
//
//	protected Module buildTestConfigModule() {
//
//		Configuration conf = new BaseConfiguration();
//		// hard-coded config
//		conf.setProperty("server.port", 0);
//		// persistence
//		conf.setProperty("db.user", "sa");
//		conf.setProperty("db.password", "");
//		conf.setProperty("db.driver", "org.hsqldb.jdbcDriver");
//		conf.setProperty("db.url", "jdbc:hsqldb:.");
//		// additional Hibernate properties
//		conf.setProperty("db.ext.hibernate.dialect",
//				"org.hibernate.dialect.HSQLDialect");
//
//		TestConfigModule confModule = new TestConfigModule(conf);
//		return confModule;
//	}
//
//	@Test
//	public void testObtainFreeGift() {
//		// prepare data
//		// 111111 - 合法QQVIP
//		// 123456 - 无效
//		String validKey = "11111111111234";
//		String invalidKey = "12345678901234";
//
//		generateMember(validKey);
//
//		// Return code definition
//		// 1 - 成功<br/>
//		// 2 - 会员Key无效<br/>
//		// 3 - 已经领取过<br/>
//
//		// case1: 123456 无效
//		assertEquals(2, obtainGift(invalidKey));
//
//		// case2: 111111 成功
//		assertEquals(1, obtainGift(validKey));
//
//		// case3: 111111 已送
//		assertEquals(3, obtainGift(validKey));
//	}
//
//	@Test
//	public void testObtainPrivilege() {
//		// prepare data
//		// 111111/222222/333333 - 合法QQVIP
//		// 123456 - 非法
//		String validKey1 = "11111111111234";
//		String validKey2 = "22222222221234";
//		String validKey3 = "33333333331234";
//		String invalidKey = "12345678901234";
//		generateMember(validKey1);
//		generateMember(validKey2);
//		generateMember(validKey3);
//
//		// return code definition!
//		// 1 - 50元现金抵用劵<br/>
//		// 2 - 100元现金抵用劵<br/>
//		// 3 - 会员key无效 <br/>
//		// 4 - 没有优惠-消费金额不够 <br/>
//		// 5 - 没有优惠-优惠已经领完<br/>
//
//		// key - consume amount - result
//		// case1: 123456 - 0 - 无效
//		assertEquals(3, redeemPrivilege(invalidKey, 0));
//
//		// case2: 111111 - 30 - 没有优惠
//		assertEquals(4, redeemPrivilege(validKey1, 30));
//
//		// case3: 111111 - 299 - 没有优惠
//		assertEquals(4, redeemPrivilege(validKey1, 299));
//
//		// case4: 111111 - 300 - 50元现金抵用劵
//		assertEquals(1, redeemPrivilege(validKey1, 300));
//
//		// case5: 111111 - 599 - 50元现金抵用劵
//		assertEquals(1, redeemPrivilege(validKey1, 599));
//
//		// case6: 111111 - 600 - 没有优惠
//		assertEquals(5, redeemPrivilege(validKey1, 600));
//
//		// case7: 222222 - 600 - 100元现金抵用劵
//		assertEquals(2, redeemPrivilege(validKey2, 600));
//
//		// case8: 222222 - 352 - 没有优惠
//		assertEquals(5, redeemPrivilege(validKey2, 600));
//
//		// case9: 222222 - 625 - 没有优惠
//		assertEquals(5, redeemPrivilege(validKey2, 625));
//
//		// case10: 333333 - 310 - 50元现金抵用劵
//		assertEquals(1, redeemPrivilege(validKey3, 310));
//
//		// case11: 333333 - 600 - 50元现金抵用劵
//		assertEquals(1, redeemPrivilege(validKey3, 600));
//	}
//
//	/**
//	 * 
//	 * @param memberKey
//	 * @return 1 - 成功<br/>
//	 *         2 - 会员Key无效<br/>
//	 *         3 - 已经领取过<br/>
//	 */
//	private int obtainGift(String memberKey) {
//		String posId = "CR-000000001";
//		try {
//			getManager().obtainFreeGift(memberKey, posId);
//			return 1;
//		} catch (InvalidMemberKeyException e) {
//			return 2;
//		} catch (DuplicateObtainGiftException e) {
//			return 3;
//		}
//	}
//
//	/**
//	 * 
//	 * @param memberKey
//	 * @param consumeAmount
//	 * @return 1 - 50元现金抵用劵<br/>
//	 *         2 - 100元现金抵用劵<br/>
//	 *         3 - 会员key无效 <br/>
//	 *         4 - 没有优惠-消费金额不够 <br/>
//	 *         5 - 没有优惠-优惠已经领完<br/>
//	 *         -1 - 未知<br/>
//	 */
//	private int redeemPrivilege(String memberKey, double consumeAmount) {
//		String posId = "CR-000000001";
//		try {
//			QQActivityHistory history = getManager().obtainPrivilege(memberKey,
//					consumeAmount, posId);
//			if (50d == history.getRebateAmt()) {
//				return 1;
//			} else if (100d == history.getRebateAmt()) {
//				return 2;
//			} else {
//				return -1;
//			}
//		} catch (InvalidMemberKeyException e) {
//			return 3;
//		} catch (ConsumeAmountNotEnoughException e) {
//			return 4;
//		} catch (PrivilegeDoneException e) {
//			return 5;
//		}
//	}
//
//	private void generateMember(String memberKey) {
//		try {
//			getInjector().getInstance(QQActivityMemberService.class)
//					.generateQQActivityMember(memberKey, new Date());
//		} catch (MemberKeyExistedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private QQAdidasActivityManager getManager() {
//		return getInjector().getInstance(QQAdidasActivityManager.class);
//	}
//}
