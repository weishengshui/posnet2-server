package com.chinarewards.posnet.ws.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * description：DES-ECB加密/解密
 * @copyright binfen.cc
 * @projectName ws-ext
 * @time 2012-4-20   下午02:45:15
 * @author Seek
 */
public final class DESECBUtil {
	
	private static final String ALGORITHM_DES = "DES/ECB/PKCS5Padding";
	
    /** 
     * DES算法，加密 
     * @param data 待加密字符串 
     * @param key  加密私钥，长度不能够小于8位 
     * @return 加密后的字节数组，一般结合Base64编码使用 
     * @throws CryptException 异常 
     */  
	public static byte[] encrypt(byte[] data, String key) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			// IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			// AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] bytes = cipher.doFinal(data);
			return bytes;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
  
    /** 
     * DES算法，解密 
     * 
     * @param data 待解密字符串 
     * @param key  解密私钥，长度不能够小于8位 
     * @return 解密后的字节数组 
     * @throws Exception 异常 
     */  
	public static byte[] decrypt(byte[] data, String key) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
//			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
//			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		final String secretkey = "abcd8855";
		// FmGrV9bwth7NxAzK5YB7tQ==
		String encrypt_res = new String(Base64.encodeBase64(encrypt(
				"hello world!".getBytes(), secretkey)));

		String decrypt_res = new String(decrypt(
				Base64.decodeBase64(encrypt_res.getBytes()), secretkey));

		System.out.println("encrypt_res:" + encrypt_res);
		System.out.println("decrypt_res:" + decrypt_res);
	}
	
}
