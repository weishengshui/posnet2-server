package com.chinarewards.qqgbvpn.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.chinarewards.qqgbvpn.main.util.HMAC_MD5;

public class MockInit {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 1234);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));
		byte[] challenge = new byte[8];
		while (true) {
			String str = wt.readLine();
			if ("init".equals(str)) {
				oldPosInit(os, is, challenge);
			} else if ("login".equals(str)) {
				oldPosLogin(os, is, challenge);
			} else {
				break;
			}
		}
	}

	static void posInitAndLogin(OutputStream os, InputStream is)
			throws Exception {
		// init and login
		byte[] challenge = new byte[8];
		oldPosInit(os, is, challenge);
		oldPosLogin(os, is, challenge);
	}

	static void oldPosInit(OutputStream os, InputStream is, byte[] challenge)
			throws Exception {
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
				0, 0, 0, 32,
				// command ID
				0, 0, 0, 5,
				// POS ID
				'R', 'E', 'W', 'A', 'R', 'D', 'S', '-', '0', '0', '0', '3' };

		// calculate checksum
		int checksum = Tools.checkSum(msg, msg.length);
		Tools.putUnsignedShort(msg, checksum, 10);

		// send both message at once
		byte[] outBuf = new byte[msg.length];
		System.arraycopy(msg, 0, outBuf, 0, msg.length);

		// write response
		os.write(outBuf);

		// ----------

		// session.write("Client First Message");
		// read
		byte[] response = new byte[30];
		int n = is.read(response);
		System.out.println("Number of bytes init read: " + n);
		for (int i = 0; i < n; i++) {
			String s = Integer.toHexString((byte) response[i]);
			if (s.length() < 2)
				s = "0" + s;
			if (s.length() > 2)
				s = s.substring(s.length() - 2);
			System.out.print(s + " ");
			if ((i + 1) % 8 == 0)
				System.out.println("");
		}
		System.arraycopy(response, 22, challenge, 0, 8);
	}

	static void oldPosLogin(OutputStream os, InputStream is, byte[] challenge)
			throws Exception {
		byte[] msg = new byte[] {
				// SEQ
				0, 0, 0, 25,
				// ACK
				0, 0, 0, 0,
				// flags
				0, 0,
				// checksum
				0, 0,
				// message length
				0, 0, 0, 48,
				// command ID
				0, 0, 0, 7,
				// POS ID
				'R', 'E', 'W', 'A', 'R', 'D', 'S', '-', '0', '0', '0', '3',
				// challengeResponse
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		byte[] content2 = HMAC_MD5.getSecretContent(challenge, "000001");
		Tools.putBytes(msg, content2, 32);
		int checksum = Tools.checkSum(msg, msg.length);
		Tools.putUnsignedShort(msg, 1, 10);
		// System.out.println("--------------------");
		// for (int i = 0; i < msg.length; i++) {
		// String s = Integer.toHexString((byte) msg[i]);
		// if (s.length() < 2)
		// s = "0" + s;
		// if (s.length() > 2)
		// s = s.substring(s.length() - 2);
		// System.out.print(s + " ");
		// if ((i + 1) % 8 == 0)
		// System.out.println("");
		// }
		// System.out.println("--------------------");
		// send both message at once
		byte[] outBuf = new byte[msg.length];
		System.arraycopy(msg, 0, outBuf, 0, msg.length);

		os.write(outBuf);
		// ----------

		// read
		byte[] response = new byte[30];
		int n = is.read(response);
		System.out.println("Number of bytes login read2: " + n);
		for (int i = 0; i < n; i++) {
			String s = Integer.toHexString((byte) response[i]);
			if (s.length() < 2)
				s = "0" + s;
			if (s.length() > 2)
				s = s.substring(s.length() - 2);
			System.out.print(s + " ");
			if ((i + 1) % 8 == 0)
				System.out.println("");
		}

	}
}
