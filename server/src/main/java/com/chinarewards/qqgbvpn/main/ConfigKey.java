/**
 * 
 */
package com.chinarewards.qqgbvpn.main;

/**
 * Defines a well known list of configuration keys.
 * 
 * @author cyril
 * @since 0.1.0
 */
public abstract class ConfigKey {

	/**
	 * The thread pool size which defines the number of threads created for
	 * handling client commands.
	 * 
	 * @since 0.1.0
	 */
	public static final String SERVER_SERVICEHANDLER_THREADPOOLSIZE = "server.service_handler.thread_pool_size";

	/**
	 * The thread pool size which defines the number of threads created for
	 * handling client commands.
	 * 
	 * @since 0.1.0
	 */
	public static final String SERVER_CLIENTMAXIDLETIME = "server.client_max_idle_time";

	/**
	 * Every several seconds (this value), server should trigger server filter's
	 * sessionIdle() method to check whether this session had been idle.
	 */
	public static final String SERVER_CHECK_IDLE_INTERVAL = "server.check.idle.interval";

	/**
	 * 这个是用来配置监控端口用的
	 * 
	 * @since 0.1.0
	 */
	public static final String SERVER_MONITORPORT = "server.monitor_port";

	/**
	 * 定时清理session store里面过期的session key 的信息（秒计 ）
	 * 
	 * @since 0.1.0
	 */
	public static final String SERVER_SESSION_TIMEOUT_CHECK_INTERVAL = "server.session.timeout_check_interval";

	/**
	 * session key 的过期时间 （秒计 ）
	 */
	public static final String SERVER_SESSION_CLIENT_TIMEOUT = "server.session.client_timeout";

	/**
	 * 这个是开关检查checksum 0=检查，1=不检查
	 */
	public static final String SERVER_DISABLE_CHECKSUM_CHECK = "server.disable_checksum_check";

	/**
	 * 这个是开关检查Challenge 0=检查，1=不检查
	 */
	public static final String SERVER_DISABLE_CHALLENGE_CHECK = "server.disable_challenge_check";

	/**
	 * Jmx rmi server hostname
	 */
	public static final String JMX_RMI_SERVER_HOSTNAME = "server.jmx.rmi.hostname";

	/**
	 * Whether jmx system mxbean register, value: true/false
	 */
	public static final String SERVER_JMX_SYSTEM_MXBEAN_MONITOR = "server.jmx.system.mxbean.monitor";

}
