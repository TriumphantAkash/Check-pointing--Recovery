package models;

import java.io.Serializable;

public class Node implements Serializable{
	private String hostName;
	private int port;
	private REBmode rebMode;
	private int totalMessageSent;
	
	public REBmode getREBmode(){
		return rebMode;
	}
	
	public void setREBmode(REBmode rebMode){
		this.rebMode = rebMode;
	}
	
	public String getHostName() {
		return hostName;
	}



	public void setHostName(String hostName) {
		this.hostName = hostName;
	}



	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}



	public int getTotalMessageSent() {
		return totalMessageSent;
	}



	public void setTotalMessageSent(int totalMessageSent) {
		this.totalMessageSent = totalMessageSent;
	}



	public enum REBmode {
	    ACTIVE, PASSIVE 
	}
}
