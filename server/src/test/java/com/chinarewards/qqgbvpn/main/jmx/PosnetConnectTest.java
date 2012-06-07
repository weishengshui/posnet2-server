package com.chinarewards.qqgbvpn.main.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.mxBean.IPosnetConnectionMXBean;
import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClient;
import com.chinarewards.qqgbvpn.main.mxBean.vo.StatCountByTime;
import com.chinarewards.qqgbvpn.main.protocol.filter.IdleConnectionKillerFilter;

// When you test, should open @Test, here comment it just because speed up
// "mvn clean install"!
/**
 * 
 * When you test, should open @Test, here comment it just because speed up
 * "mvn clean install"! Too more Thread.sleep()...
 * 
 * @author yanxin
 * @since 0.3.3
 */
public class PosnetConnectTest extends JmxBaseTest {

	private Socket newConnection() throws Exception {
		Socket socket = new Socket("localhost", port);
		return socket;
	}

	private byte[] init(Socket s) throws Exception {
		byte[] challenge = new byte[8];
		oldPosInit(s.getOutputStream(), s.getInputStream(), challenge);

		return challenge;
	}

	private void login(Socket s, byte[] challenge) throws Exception {
		oldPosLogin(s.getOutputStream(), s.getInputStream(), challenge);
	}

	@Test
	public void testConnect_Init() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();

		// ----------------- check
		assertEquals(0, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(0, mxBean.getBytesReceived());
		assertEquals(0, mxBean.getBytesSent());
		assertEquals(0, mxBean.getKnownClientCount());

		// init s1
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		// ----------------- check
		assertEquals(1, mxBean.getConnectionCount());
		assertEquals(1, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);

			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	@Test
	public void testConnect_AppearIdle() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		// sleep 4s
		Thread.sleep(4 * 1000);

		// check
		assertEquals(1, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(1, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);
			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	@Test
	public void testConnect_AppearIdleThenActiveAgain() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();
		long initSentBefore = System.currentTimeMillis();
		byte[] challenge = init(s1);
		long initSentAfter = System.currentTimeMillis();

		// sleep 4s
		Thread.sleep(4 * 1000);

		// check
		assertEquals(1, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(1, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);
			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		// login make idle become active again!
		long loginSentBefore = System.currentTimeMillis();
		login(s1, challenge);
		long loginSentAfter = System.currentTimeMillis();

		// check
		assertEquals(1, mxBean.getConnectionCount());
		assertEquals(1, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(32 + 48, mxBean.getBytesReceived());
		assertEquals(30 + 30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		clients = mxBean.getKnownClients();
		it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= loginSentBefore
					&& client.getLastDataReceivedAt().getTime() <= loginSentAfter);
			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	@Test
	public void testKnownClient_IdleDropped() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);
		IdleConnectionKillerFilter killerFilter = getInjector().getInstance(
				IdleConnectionKillerFilter.class);
		killerFilter.setIdleTime(2);

		// create first connection
		Socket s1 = newConnection();
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		// sleep 8s
		Thread.sleep(8 * 1000);

		// check
		assertEquals(0, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);
			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(1, idleCount.getToday());
			assertEquals(1, idleCount.getLast7Days());
			assertEquals(1, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	@Test
	public void testKnowClient_BadDataDropped() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		sendBadData(s1);
		sendBadData(s1);
		sendBadData(s1);
		sendBadData(s1);
		// The fifth send bad data will result the connection closed!
		long lastSentBefore = System.currentTimeMillis();
		sendBadData(s1);
		long lastSentAfter = System.currentTimeMillis();

		// check
		assertEquals(0, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(32 + 16 * 5, mxBean.getBytesReceived());
		assertEquals(30 + 24 * 4, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= lastSentBefore
					&& client.getLastDataReceivedAt().getTime() <= lastSentAfter);
			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(1, badDataCount.getToday());
			assertEquals(1, badDataCount.getLast7Days());
			assertEquals(1, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	private void sendBadData(Socket s) throws Exception {
		byte[] msg = new byte[] {
				// SEQ
				0, 0, 0, 24,
				// ACK
				0, 0, 0, 0,
				// flags
				0, 0,
				// checksum
				0, 0,
				// message length
				0, 0, 0, 16 };

		// calculate checksum
		int checksum = Tools.checkSum(msg, msg.length);
		Tools.putUnsignedShort(msg, checksum, 10);

		// send both message at once
		byte[] outBuf = new byte[msg.length];
		System.arraycopy(msg, 0, outBuf, 0, msg.length);

		// write response
		log.info(" Send Bad request to server");
		s.getOutputStream().write(outBuf);

		Thread.sleep(1000);

		byte[] response = new byte[30];
		int n = s.getInputStream().read(response);
		System.out.println("Number of bytes init read: " + n);
		for (int i = 0; i < n; i++) {
			String str = Integer.toHexString((byte) response[i]);
			if (str.length() < 2)
				str = "0" + s;
			if (str.length() > 2)
				str = str.substring(str.length() - 2);
			System.out.print(str + " ");
			if ((i + 1) % 8 == 0)
				System.out.println("");
		}
	}

	@Test
	public void testResetStatisgtics() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();

		// init s1
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		mxBean.resetStatistics();

		// ----------------- check
		assertEquals(1, mxBean.getConnectionCount());
		assertEquals(1, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(0, mxBean.getBytesReceived());
		assertEquals(0, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);

			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	@Test
	public void testCloseIdleConnections() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();

		// init s1
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		// sleep 7s, make this session become idle.
		Thread.sleep(7 * 1000);

		mxBean.closeIdleConnections();

		// ----------------- check
		assertEquals(0, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);

			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(1, idleCount.getToday());
			assertEquals(1, idleCount.getLast7Days());
			assertEquals(1, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}

	@Test
	public void testCloseIdleConnections_SettingIdleTime() throws Exception {
		// Get target mx bean.
		IPosnetConnectionMXBean mxBean = getInjector().getInstance(
				IPosnetConnectionMXBean.class);

		// create first connection
		Socket s1 = newConnection();

		// init s1
		long initSentBefore = System.currentTimeMillis();
		init(s1);
		long initSentAfter = System.currentTimeMillis();

		// sleep 5s, make this session become idle. The idle interval is 1s,
		// means idle start time is 4s ago!
		Thread.sleep(5 * 1000);

		// It means the connection whose idle time >= 4s should be closed!
		// Obviously, our connection will live!
		mxBean.closeIdleConnections(4);

		// ----------------- check
		assertEquals(1, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(1, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		Map<String, KnownClient> clients = mxBean.getKnownClients();
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);

			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(0, idleCount.getToday());
			assertEquals(0, idleCount.getLast7Days());
			assertEquals(0, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		// It means the connection whose idle time >= 1s should be closed!
		// Obviously, our connection will be killed!
		mxBean.closeIdleConnections(1);

		// ----------------- check
		assertEquals(0, mxBean.getConnectionCount());
		assertEquals(0, mxBean.getActiveConnectionCount());
		assertEquals(0, mxBean.getIdleConnectionCount());
		assertEquals(32, mxBean.getBytesReceived());
		assertEquals(30, mxBean.getBytesSent());
		assertEquals(1, mxBean.getKnownClientCount());
		clients = mxBean.getKnownClients();
		it = clients.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			assertEquals("REWARDS-0003", s);
			KnownClient client = clients.get(s);
			String activeIp = client.getIp();
			assertEquals("/127.0.0.1",
					activeIp.substring(0, activeIp.indexOf(":")));
			assertTrue(client.getLastConnectedAt().getTime() >= initSentBefore
					&& client.getLastConnectedAt().getTime() <= initSentAfter);
			assertTrue(client.getLastDataReceivedAt().getTime() >= initSentBefore
					&& client.getLastDataReceivedAt().getTime() <= initSentAfter);

			StatCountByTime idleCount = client.getIdleConnectionDropStats();
			assertEquals(1, idleCount.getToday());
			assertEquals(1, idleCount.getLast7Days());
			assertEquals(1, idleCount.getTotal());
			StatCountByTime badDataCount = client
					.getCorruptDataConnectionDropStats();
			assertEquals(0, badDataCount.getToday());
			assertEquals(0, badDataCount.getLast7Days());
			assertEquals(0, badDataCount.getTotal());
		}

		s1.getOutputStream().close();
		s1.close();
	}
}
