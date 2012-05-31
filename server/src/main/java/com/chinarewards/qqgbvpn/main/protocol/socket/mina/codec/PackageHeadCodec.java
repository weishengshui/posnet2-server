package com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.protocol.cmd.HeadMessage;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.session.CodecException;
import com.chinarewards.qqgbvpn.main.session.SessionKeyCodec;

public class PackageHeadCodec {
	
	private static Logger log = LoggerFactory.getLogger(PackageHeadCodec.class);
	
	private SessionKeyCodec sessionKeyCodec;
	
	public PackageHeadCodec() {
		this.sessionKeyCodec = new SessionKeyCodec();
	}
		
	/**
	 * Description：encode package head message
	 * 
	 * @param headMessage
	 * @return
	 * @time 2011-9-22   下午08:09:04
	 * @author Seek
	 */
	public byte[] encode(HeadMessage headMessage) {
		byte[] headByte = new byte[ProtocolLengths.HEAD];

		Tools.putUnsignedInt(headByte, headMessage.getSeq(), 0);
		Tools.putUnsignedInt(headByte, headMessage.getAck(), 4);
		Tools.putUnsignedShort(headByte, headMessage.getFlags(), 8);
		Tools.putUnsignedShort(headByte, 0, 10);	//checksum
		Tools.putUnsignedInt(headByte, headMessage.getMessageSize(),
				12);
		
		return headByte;
	}
	
	/**
	 * Decode head message from the IoBuffer. This method will <code>not</code>
	 * reset IoBuffer's original position.
	 * 
	 * @param in
	 * @return
	 */
	public HeadMessage decode(IoBuffer in) {
		
		HeadMessage headMessage = new HeadMessage();
		
		/***** standard set of fields to decode *****/
		
		// read header
		// read sequence
		long seq = in.getUnsignedInt();
		headMessage.setSeq(seq);

		// read ack
		long ack = in.getUnsignedInt();
		headMessage.setAck(ack);

		// read flags
		int flags = in.getUnsignedShort();
		headMessage.setFlags(flags);

		// read checksum
		int checksum = in.getUnsignedShort();
		headMessage.setChecksum(checksum);

		// read message size
		long messageSize = in.getUnsignedInt();
		headMessage.setMessageSize(messageSize);
		
		/***** standard set of fields to decode *****/
		
		return headMessage;
	}
	
}
