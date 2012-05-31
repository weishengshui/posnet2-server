package com.chinarewards.qqgbvpn.main.util;

import java.nio.charset.Charset;

public class Str2ByteUtil {

	public static int getLen(String str, Charset charset) {
		if (str == null || "".equals(str)) {
			return 0;
		} else {
			return str.getBytes(charset).length;
		}
	}

	public static byte[] str2Byte(String str, Charset charset) {
		if (str == null || "".equals(str)) {
			return null;
		} else {
			return str.getBytes(charset);
		}
	}
}
