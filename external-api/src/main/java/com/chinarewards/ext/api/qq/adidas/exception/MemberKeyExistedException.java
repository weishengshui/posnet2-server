package com.chinarewards.ext.api.qq.adidas.exception;

/**
 * The memberKey has existed!
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class MemberKeyExistedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5556625882667841398L;

	public MemberKeyExistedException() {
		super();
	}

	public MemberKeyExistedException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemberKeyExistedException(String message) {
		super(message);
	}

	public MemberKeyExistedException(Throwable cause) {
		super(cause);
	}

}
