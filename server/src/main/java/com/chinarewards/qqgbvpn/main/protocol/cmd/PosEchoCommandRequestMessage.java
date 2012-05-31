package com.chinarewards.qqgbvpn.main.protocol.cmd;

import java.util.Arrays;

/**
 * description：pos request echo command
 * @copyright binfen.cc
 * @projectName main
 * @time 2011-10-20   下午06:02:22
 * @author Seek
 */
public class PosEchoCommandRequestMessage implements ICommand {
	
	private final long cmdId = CmdConstant.ECHO_CMD_ID;
	
	private byte[] data;

	@Override
	public String toString() {
		return " [cmdId=" + cmdId + ", data=" + Arrays.toString(data) + "]";
	}

	public long getCmdId() {
		return cmdId;
	}
	
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
}
