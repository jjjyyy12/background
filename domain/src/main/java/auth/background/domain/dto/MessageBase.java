package auth.background.domain.dto;

import java.io.Serializable;

public class MessageBase implements Serializable {
	 private byte[] MessageBodyByte;
	 private byte[] MessageBodyReturnByte;
	 private String MessageRouter;
	 private int FailedTimes = 0;
	 private String exchangeName;

	public byte[] getMessageBodyByte() {
		return MessageBodyByte;
	}

	public void setMessageBodyByte(byte[] messageBodyByte) {
		MessageBodyByte = messageBodyByte;
	}

	public byte[] getMessageBodyReturnByte() {
		return MessageBodyReturnByte;
	}

	public void setMessageBodyReturnByte(byte[] messageBodyReturnByte) {
		MessageBodyReturnByte = messageBodyReturnByte;
	}

	public String getMessageRouter() {
		return MessageRouter;
	}

	public void setMessageRouter(String messageRouter) {
		MessageRouter = messageRouter;
	}

	public int getFailedTimes() {
		return FailedTimes;
	}

	public void setFailedTimes(int failedTimes) {
		FailedTimes = failedTimes;
	}

	public String getexchangeName() {
		return exchangeName;
	}

	public void setexchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
     
}
