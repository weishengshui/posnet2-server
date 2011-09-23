package com.chinarewards.qqgbpvn.testing.context;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.chinarewards.qqgbpvn.testing.model.BasePosConfig;
import com.chinarewards.qqgbvpn.main.protocol.SimpleCmdCodecFactory;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.PackageHeadCodec;

/**
 * description：基础环境配置
 * @copyright binfen.cc
 * @projectName test-qqGBV
 * @time 2011-9-22   下午02:18:14
 * @author Seek
 */
public class TestContext {
	
	private static final Map<String, BasePosConfig> POS_MAP = Collections.
								synchronizedMap(new HashMap<String, BasePosConfig>());
	
	private static final ThreadLocal<BasePosConfig> tLocal = 
								new ThreadLocal<BasePosConfig>();
	
	private static Charset charset = null;
	
	private static Long maxPos = 0L;
	
	private static Long num = 0L;
	
	//POS Server IP
	private static String posServerIp;
	
	//POS Server Port
	private static String posServerPort;
	
	private static SimpleCmdCodecFactory cmdCodecFactory;	//codec by cmdId
	
	private static PackageHeadCodec packageHeadCodec;
	
	/**
	 * description：销毁test系统级的数据
	 * @time 2011-9-22   下午03:48:32
	 * @author Seek
	 */
	public static void testDestroy(){
		maxPos = 0L;
		num = 0L;
		POS_MAP.clear();
		charset = null;
		posServerIp = null;
		posServerPort = null;
		cmdCodecFactory = null;
		packageHeadCodec = null;
	}
	
	/**
	 * description：清除自己线程的ThreadLocal
	 * @time 2011-9-22   下午03:48:15
	 * @author Seek
	 */
	public static void clearBasePosConfig(){
		if(tLocal.get() != null){
			try {
				tLocal.get().getSocket().close();	//关闭连接
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			tLocal.remove();
		}
	}
	
	/**
	 * description：初始化一个pos 线程的基础内容
	 * @throws Exception
	 * @time 2011-9-22   下午05:57:42
	 * @author Seek
	 */
	public static final synchronized void initBasePosConfig() throws Exception{
		if(num == maxPos){
			return;
		}
		
		if(tLocal.get() == null){
			num++;
			
			//get a BasePosConfig from static map, remove it!
			//save to new BasePosConfig for threadLocal.
			BasePosConfig tempBasePosConfig = getPosMap().get(num.toString());
			
			BasePosConfig basePosConfig = new BasePosConfig();
			basePosConfig.setNumber(num);
			basePosConfig.setPosId(tempBasePosConfig.getPosId());
			basePosConfig.setSecret(tempBasePosConfig.getSecret());
			basePosConfig.setSocket(new Socket(getPosServerIp(), Integer.parseInt(getPosServerPort())));
			basePosConfig.setSequence(1);
			tLocal.set(basePosConfig);
			
			getPosMap().remove(num.toString());
		}
	}
	
	/**
	 * description：自增流水号
	 * @time 2011-9-23   下午02:14:14
	 * @author Seek
	 */
	public static final void incrementSequence(){
		tLocal.get().setSequence(tLocal.get().getSequence() + 1);
	}
	
	public static final BasePosConfig getBasePosConfig(){
		return tLocal.get();
	}
	
	
	public static Map<String, BasePosConfig> getPosMap() {
		return POS_MAP;
	}
	
	public static Long getMaxPos() {
		return maxPos;
	}

	public static void setMaxPos(Long maxPos) {
		TestContext.maxPos = maxPos;
	}
	
	public static String getPosServerIp() {
		return posServerIp;
	}

	public static void setPosServerIp(String posServerIp) {
		TestContext.posServerIp = posServerIp;
	}

	public static String getPosServerPort() {
		return posServerPort;
	}

	public static void setPosServerPort(String posServerPort) {
		TestContext.posServerPort = posServerPort;
	}
	
	public static SimpleCmdCodecFactory getCmdCodecFactory() {
		return cmdCodecFactory;
	}

	public static void setCmdCodecFactory(SimpleCmdCodecFactory cmdCodecFactory) {
		TestContext.cmdCodecFactory = cmdCodecFactory;
	}
	
	public static PackageHeadCodec getPackageHeadCodec() {
		return packageHeadCodec;
	}

	public static void setPackageHeadCodec(PackageHeadCodec packageHeadCodec) {
		TestContext.packageHeadCodec = packageHeadCodec;
	}
	
	public static void setCharset(Charset charset) {
		TestContext.charset = charset;
	}

	public static Charset getCharset() {
		return charset;
	}
	
}
