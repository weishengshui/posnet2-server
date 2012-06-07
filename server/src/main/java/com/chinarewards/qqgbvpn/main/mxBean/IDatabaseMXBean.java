package com.chinarewards.qqgbvpn.main.mxBean;

/**
 * The mxbean about database connection and others.
 * 
 * @author yanxin
 * @since 0.3.2
 */
public interface IDatabaseMXBean {

	public boolean isAliveJpa();

	public boolean isAliveJdbc();
}
