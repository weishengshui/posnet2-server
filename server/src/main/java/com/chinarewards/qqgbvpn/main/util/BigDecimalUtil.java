package com.chinarewards.qqgbvpn.main.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

	public static double sub(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();
	}

	public static void main(String[] args) {
		System.out.println(BigDecimalUtil.sub(500d, 50));
		System.out.println(BigDecimalUtil.sub(16503.19d, 15595.49d));
	}
}
