package com.chinarewards.posnet.ws.vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * description：QQVIP Adidas Request VO
 * @copyright binfen.cc
 * @projectName ws-ext
 * @time 2012-4-20   下午03:49:42
 * @author Seek
 */
public class SynMemberReq implements Serializable {
	
	private static final long serialVersionUID = -8537875668938190588L;

	/**
	 * Member key which is decrypted from rawRequestCtn.
	 */
	private String memberKey;

	/**
	 * Timestamp which is decrypted from rawRequestCtn.
	 */
	private String timestamp;

	public SynMemberReq(){
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SynMemberReq))
			return false;
		SynMemberReq respVO = (SynMemberReq) obj;
		return new EqualsBuilder().append(this.memberKey, respVO.memberKey)
				.append(this.timestamp, respVO.timestamp).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.memberKey)
				.append(this.timestamp).toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("memberKey", this.memberKey)
				.append("timestamp", this.timestamp)
				.toString();
	}
	
	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
}
