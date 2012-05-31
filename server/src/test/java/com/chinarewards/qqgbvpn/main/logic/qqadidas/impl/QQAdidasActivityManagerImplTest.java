package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import com.chinarewards.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ext.api.qq.adidas.service.QQActivityMemberService;
import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbpvn.main.CommonTestConfigModule;
import com.chinarewards.qqgbpvn.main.TestConfigModule;
import com.chinarewards.qqgbpvn.main.test.JpaGuiceTest;
import com.chinarewards.qqgbvpn.core.jpa.JpaPersistModuleBuilder;
import com.chinarewards.qqgbvpn.domain.event.DomainEntity;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.domain.event.Journal;
import com.chinarewards.qqgbvpn.main.guice.AppModule;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityManager;
import com.chinarewards.qqgbvpn.main.module.qqadidas.QQAdidasApiModule;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainGiftVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainPrivilegeVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQWeixinSignInVo;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * 
 * @author yanxin
 * 
 */

public class QQAdidasActivityManagerImplTest extends JpaGuiceTest {

	protected Module[] getModules() {

		CommonTestConfigModule confModule = new CommonTestConfigModule();
		Configuration configuration = confModule.getConfiguration();

		JpaPersistModule jpaModule = new JpaPersistModule("posnet");
		JpaPersistModuleBuilder builder = new JpaPersistModuleBuilder();
		builder.configModule(jpaModule, configuration, "db");

		return new Module[] { new AppModule(), new QQAdidasApiModule(),
				jpaModule, confModule };
	}

	protected Module buildTestConfigModule() {

		Configuration conf = new BaseConfiguration();
		// hard-coded config
		conf.setProperty("server.port", 0);
		// persistence
		conf.setProperty("db.user", "sa");
		conf.setProperty("db.password", "");
		conf.setProperty("db.driver", "org.hsqldb.jdbcDriver");
		conf.setProperty("db.url", "jdbc:hsqldb:.");
		// additional Hibernate properties
		conf.setProperty("db.ext.hibernate.dialect",
				"org.hibernate.dialect.HSQLDialect");

		TestConfigModule confModule = new TestConfigModule(conf);
		return confModule;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWeixinSignIn_detail() {
		String weixinNo = "325435432";
		String posId = "CR-000000002";
		QQWeixinSignInVo signInVo = getManager().weiXinSignIn(weixinNo, posId);
		assertEquals(QQAdConstant.WEIXIN_SUCCESS, signInVo.getReturnCode());
		// check QQWeixinSignIn
		List<QQWeixinSignIn> weixins = getEm().createQuery(
				"FROM QQWeixinSignIn").getResultList();
		assertEquals(1, weixins.size());
		QQWeixinSignIn weixin = weixins.get(0);
		assertEquals(weixinNo, weixin.getWeixinNo());

		// test several times.
		signInVo = getManager().weiXinSignIn(weixinNo, posId);
		assertEquals(QQAdConstant.WEIXIN_SUCCESS, signInVo.getReturnCode());
		// check QQWeixinSignIn
		weixins = getEm().createQuery("FROM QQWeixinSignIn").getResultList();
		assertEquals(2, weixins.size());
		weixin = weixins.get(0);
		assertEquals(weixinNo, weixin.getWeixinNo());
		QQWeixinSignIn weixin2 = weixins.get(1);
		assertEquals(weixinNo, weixin2.getWeixinNo());
	}

	@Test
	public void testObtainFreeGift() {
		// prepare data
		// 111111 - 合法QQVIP
		// 123456 - 无效
		String validKey = "11111111111234";
		String invalidKey = "12345678901234";

		generateMember(validKey);

		// Return code definition
		// 0 - 成功<br/>
		// 1 - 会员Key无效<br/>
		// 2 - 已经领取过<br/>

		// case1: 123456 无效
		assertEquals(QQAdConstant.GIFT_FAIL_INVALID_MEMBER,
				obtainGift(invalidKey));

		// case2: 111111 成功
		assertEquals(QQAdConstant.GIFT_OK, obtainGift(validKey));

		// case3: 111111 已送
		assertEquals(QQAdConstant.GIFT_FAIL_OBTAINED_ALREADY,
				obtainGift(validKey));
	}

	@Test
	public void testObtainPrivilege() {
		// prepare data
		// 111111/222222/333333 - 合法QQVIP
		// 123456 - 非法
		String validKey1 = "11111111111234";
		String validKey2 = "22222222221234";
		String validKey3 = "33333333331234";
		String invalidKey = "12345678901234";
		generateMember(validKey1);
		generateMember(validKey2);
		generateMember(validKey3);

		// return code definition!
		// 0 - 50元现金抵用劵<br/>
		// 0 - 100元现金抵用劵<br/>
		// 1 - 会员key无效 <br/>
		// 2 - 没有优惠-消费金额不够 <br/>
		// 3 - 没有优惠-优惠已经领完<br/>

		// key - consume amount - result
		// case1: 123456 - 0 - 无效
		assertEquals(QQAdConstant.PRIVILEGE_FAIL_INVALD_MEMBER,
				redeemPrivilege(invalidKey, 0));

		// case2: 111111 - 30 - 没有优惠
		assertEquals(QQAdConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH,
				redeemPrivilege(validKey1, 30));

		// case3: 111111 - 299 - 没有优惠
		assertEquals(QQAdConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH,
				redeemPrivilege(validKey1, 299));

		// case4: 111111 - 300 - 50元现金抵用劵
		assertEquals(QQAdConstant.PRIVILEGE_OK,
				redeemPrivilege(validKey1, 300));

		// case5: 111111 - 599 - 50元现金抵用劵
		assertEquals(QQAdConstant.PRIVILEGE_OK,
				redeemPrivilege(validKey1, 599));

		// case6: 111111 - 600 - 没有优惠
		assertEquals(QQAdConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY,
				redeemPrivilege(validKey1, 600));

		// case7: 222222 - 600 - 100元现金抵用劵
		assertEquals(QQAdConstant.PRIVILEGE_OK,
				redeemPrivilege(validKey2, 600));

		// case8: 222222 - 352 - 没有优惠
		assertEquals(QQAdConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY,
				redeemPrivilege(validKey2, 600));

		// case9: 222222 - 625 - 没有优惠
		assertEquals(QQAdConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY,
				redeemPrivilege(validKey2, 625));

		// case10: 333333 - 310 - 50元现金抵用劵
		assertEquals(QQAdConstant.PRIVILEGE_OK,
				redeemPrivilege(validKey3, 310));

		// case11: 333333 - 600 - 50元现金抵用劵
		assertEquals(QQAdConstant.PRIVILEGE_OK,
				redeemPrivilege(validKey3, 600));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testObtainGift_detail() {
		// 11111111111234 成功
		String posId = "CR-0000000001";
		String validKey = "11111111111234";
		generateMember(validKey);
		QQMemberObtainGiftVo giftVo = getManager().obtainFreeGift(validKey,
				posId);
		assertEquals(QQAdConstant.GIFT_OK, giftVo.getReturnCode());
		log.debug("Receipt:{}", giftVo.getReceipt());
		// check QQActivityHistory
		List<QQActivityHistory> histories = getEm().createQuery(
				"FROM QQActivityHistory").getResultList();
		assertEquals(1, histories.size());
		QQActivityHistory history = histories.get(0);
		assertEquals(validKey, history.getMemberKey());
		assertEquals(posId, history.getPosId());
		assertEquals(ActivityType.GIFT, history.getAType());

		// check QQActivityMember
		List<QQActivityMember> members = getEm().createQuery(
				"FROM QQActivityMember").getResultList();
		assertEquals(1, members.size());
		QQActivityMember member = members.get(0);
		assertEquals(PrivilegeStatus.NEW, member.getPrivilegeStatus());
		assertEquals(GiftStatus.DONE, member.getGiftStatus());

		// check journal
		List<Journal> list = getEm().createQuery("FROM Journal")
				.getResultList();
		assertEquals(1, list.size());
		Journal j = list.get(0);
		assertEquals(DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString(), j.getEvent());
		assertEquals(DomainEntity.QQ_ACTIVITY_HISTORY.toString(), j.getEntity());
		assertNotNull(j.getEventDetail());
		assertEquals(history.getId(), j.getEntityId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testObtainPrivilege_detail() {
		// 11111111111234 - 300 - 50元现金抵用劵
		String posId = "CR-0000000001";
		String validKey = "11111111111234";
		double consumeAmt = 300d;
		generateMember(validKey);
		QQMemberObtainPrivilegeVo privilegeVo = getManager().obtainPrivilege(
				validKey, consumeAmt, posId);
		assertEquals(QQAdConstant.PRIVILEGE_OK, privilegeVo.getReturnCode());
		log.debug("Receipt:{}", privilegeVo.getReceipt().getContent());
		// check QQActivityHistory
		List<QQActivityHistory> histories = getEm().createQuery(
				"FROM QQActivityHistory").getResultList();
		assertEquals(1, histories.size());
		QQActivityHistory history = histories.get(0);
		assertEquals(validKey, history.getMemberKey());
		assertEquals(posId, history.getPosId());
		assertEquals(consumeAmt, history.getConsumeAmt(), 0);
		assertEquals(ActivityType.PRIVILEGE, history.getAType());
		assertEquals(QQAdConstant.REBATE_HALF_AMOUNT,
				history.getRebateAmt(), 0);

		// check QQActivityMember
		List<QQActivityMember> members = getEm().createQuery(
				"FROM QQActivityMember").getResultList();
		assertEquals(1, members.size());
		QQActivityMember member = members.get(0);
		assertEquals(GiftStatus.NEW, member.getGiftStatus());
		assertEquals(PrivilegeStatus.HALF, member.getPrivilegeStatus());

		// check journal
		List<Journal> list = getEm().createQuery("FROM Journal")
				.getResultList();
		assertEquals(1, list.size());
		Journal j = list.get(0);
		assertEquals(DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE.toString(),
				j.getEvent());
		assertEquals(DomainEntity.QQ_ACTIVITY_HISTORY.toString(), j.getEntity());
		assertNotNull(j.getEventDetail());
		assertEquals(history.getId(), j.getEntityId());
	}

	/**
	 * 
	 * @param memberKey
	 * @return
	 */
	private int obtainGift(String memberKey) {
		String posId = "CR-000000001";
		QQMemberObtainGiftVo giftVo = getManager().obtainFreeGift(memberKey,
				posId);
		return giftVo.getReturnCode();
	}

	/**
	 * 
	 * @param memberKey
	 * @param consumeAmount
	 * @return
	 */
	private int redeemPrivilege(String memberKey, double consumeAmount) {
		String posId = "CR-000000001";
		QQMemberObtainPrivilegeVo privilegeVo = getManager().obtainPrivilege(
				memberKey, consumeAmount, posId);
		return privilegeVo.getReturnCode();
	}

	private void generateMember(String memberKey) {
		try {
			getInjector().getInstance(QQActivityMemberService.class)
					.generateQQActivityMember(memberKey, new Date());
		} catch (MemberKeyExistedException e) {
			e.printStackTrace();
		}
	}

	private QQAdActivityManager getManager() {
		return getInjector().getInstance(QQAdActivityManager.class);
	}
}
