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
	 * 这个是用来配置监控端口用的
	 * 
	 * @since 0.1.0
	 */
	public static final String SERVER_MONITORPORT = "server.monitor_port";

}
