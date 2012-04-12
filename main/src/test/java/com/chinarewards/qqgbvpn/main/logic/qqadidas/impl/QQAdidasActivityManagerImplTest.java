//package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//import java.util.Date;
//
//import org.apache.commons.configuration.BaseConfiguration;
//import org.apache.commons.configuration.Configuration;
//import org.junit.Test;
//
//import com.chinarewards.qq.adidas.domain.GiftStatus;
//import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
//import com.chinarewards.qq.adidas.domain.QQActivityHistory;
//import com.chinarewards.qq.adidas.domain.QQActivityMember;
//import com.chinarewards.qqgbpvn.main.CommonTestConfigModule;
//import com.chinarewards.qqgbpvn.main.TestConfigModule;
//import com.chinarewards.qqgbpvn.main.test.JpaGuiceTest;
//import com.chinarewards.qqgbvpn.core.jpa.JpaPersistModuleBuilder;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateAchievingGiftException;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
//import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;
//import com.chinarewards.qqgbvpn.main.guice.AppModule;
//import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
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
//		return new Module[] { new AppModule(), jpaModule, confModule };
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
//	public void testAchieveFreeGift() {
//		// prepare data
//		// 111111 - 合法QQVIP
//		// 123456 - 无效
//		String validKey = "111111";
//		String invalidKey = "123456";
//
//		generateMember(validKey);
//
//		// case1: 123456 无效
//		try {
//			getManager().obtainFreeGift(invalidKey);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		} catch (DuplicateAchievingGiftException e) {
//			fail("Can not go here!");
//		}
//
//		// case2: 111111 成功
//		try {
//			QQActivityHistory history = getManager().obtainFreeGift(validKey);
//			log.debug("Should run here!");
//			assertNotNull(history);
//			assertNotNull(history.getId());
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (DuplicateAchievingGiftException e) {
//			fail("Can not go here!");
//		}
//
//		// case3: 111111 已送
//		try {
//			getManager().obtainFreeGift(validKey);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (DuplicateAchievingGiftException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		}
//	}
//
//	@SuppressWarnings("deprecation")
//	@Test
//	public void testAchievePrivilege() {
//		// prepare data
//		// 111111/222222/333333 - 合法QQVIP
//		// 123456 - 非法
//		String validKey1 = "111111";
//		String validKey2 = "222222";
//		String validKey3 = "333333";
//		String invalidKey = "123456";
//		generateMember(validKey1);
//		generateMember(validKey2);
//		generateMember(validKey3);
//		// key - consume amount - result
//		// case1: 123456 - 0 - 无效
//		try {
//			getManager().obtainPrivilege(invalidKey, 0);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case2: 111111 - 30 - 没有优惠
//		try {
//			getManager().obtainPrivilege(validKey1, 30);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case3: 111111 - 299 - 没有优惠
//		try {
//			getManager().obtainPrivilege(validKey1, 299);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case4: 111111 - 300 - 50元现金抵用劵
//		try {
//			QQActivityHistory history = getManager().obtainPrivilege(
//					validKey1, 300);
//			assertEquals(50d, history.getRebateAmt());
//			log.debug("Should run here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case5: 111111 - 599 - 50元现金抵用劵
//		try {
//			QQActivityHistory history = getManager().obtainPrivilege(
//					validKey1, 599);
//			assertEquals(50d, history.getRebateAmt());
//			log.debug("Should run here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case6: 111111 - 600 - 没有优惠
//		try {
//			getManager().obtainPrivilege(validKey1, 600);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		}
//
//		// case7: 222222 - 600 - 100元现金抵用劵
//		try {
//			QQActivityHistory history = getManager().obtainPrivilege(
//					validKey2, 600);
//			assertEquals(100d, history.getRebateAmt());
//			log.debug("Should run here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case8: 222222 - 352 - 没有优惠
//		try {
//			getManager().obtainPrivilege(validKey2, 352);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		}
//
//		// case9: 222222 - 625 - 没有优惠
//		try {
//			getManager().obtainPrivilege(validKey2, 625);
//			fail("Can not go here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			log.debug("Should run here!");
//			assertTrue(true);
//		}
//
//		// case10: 333333 - 310 - 50元现金抵用劵
//		try {
//			QQActivityHistory history = getManager().obtainPrivilege(
//					validKey3, 310);
//			assertEquals(50d, history.getRebateAmt());
//			log.debug("Should run here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//
//		// case10: 333333 - 600 - 50元现金抵用劵
//		try {
//			QQActivityHistory history = getManager().obtainPrivilege(
//					validKey3, 600);
//			assertEquals(50d, history.getRebateAmt());
//			log.debug("Should run here!");
//		} catch (InvalidMemberKeyException e) {
//			fail("Can not go here!");
//		} catch (ConsumeAmountNotEnoughException e) {
//			fail("Can not go here!");
//		} catch (PrivilegeDoneException e) {
//			fail("Can not go here!");
//		}
//	}
//
//	private void generateMember(String memberKey) {
//		Date now = new Date();
//		QQActivityMember member = new QQActivityMember();
//		member.setMemberKey(memberKey);
//		member.setGiftStatus(GiftStatus.NEW);
//		member.setPrivilegeStatus(PrivilegeStatus.NEW);
//		member.setSendTime(now);
//		member.setCreatedAt(now);
//		member.setLastModifiedAt(now);
//		getEm().persist(member);
//	}
//
//	private QQAdidasActivityManager getManager() {
//		return getInjector().getInstance(QQAdidasActivityManager.class);
//	}
//}
