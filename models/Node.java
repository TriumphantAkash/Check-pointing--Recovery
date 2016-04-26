package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import models.Message.REBmode;

public class Node implements Serializable{
	private int nodeId;
	private String hostName;
	private int port;
	private REBmode rebMode = REBmode.PASSIVE;
	private int totalMessageSent;
	private HashMap<Integer, Node> neighbours = new HashMap<Integer, Node>();
	
	private TreeMap<Integer, Integer> SENT_VECTOR= new TreeMap<Integer, Integer>();
	private TreeMap<Integer, Integer> RCVD_VECTOR= new TreeMap<Integer, Integer>();
	
	//Checkpoint vector clock
	private ArrayList<Integer> vectorClock = new ArrayList<Integer>();
	

	
	public synchronized TreeMap<Integer, Integer> getSENT_VECTOR() {
		return SENT_VECTOR;
	}

	public synchronized void setSENT_VECTOR(TreeMap<Integer, Integer> sENT_VECTOR) {
		SENT_VECTOR = sENT_VECTOR;
	}

	public synchronized TreeMap<Integer, Integer> getRCVD_VECTOR() {
		return RCVD_VECTOR;
	}

	public synchronized void setRCVD_VECTOR(TreeMap<Integer, Integer> rCVD_VECTOR) {
		RCVD_VECTOR = rCVD_VECTOR;
	}

	public synchronized ArrayList<Integer> getVectorClock() {
		return vectorClock;
	}

	public synchronized void setVectorClock(ArrayList<Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}
	
	public synchronized HashMap<Integer, Node> getNeighbours() {
		return neighbours;
	}

	public synchronized void setNeighbours(HashMap<Integer, Node> neighbours) {
		this.neighbours = neighbours;
	}

	public synchronized int getNodeId() {
		return nodeId;
	}

	public synchronized void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public synchronized REBmode getREBmode(){
		return rebMode;
	}
	
	public synchronized void setREBmode(REBmode rebMode){
		this.rebMode = rebMode;
	}
	
	public synchronized String getHostName() {
		return hostName;
	}



	public synchronized void setHostName(String hostName) {
		this.hostName = hostName;
	}



	public synchronized int getPort() {
		return port;
	}



	public synchronized void setPort(int port) {
		this.port = port;
	}



	public synchronized int getTotalMessageSent() {
		return totalMessageSent;
	}



	public synchronized void setTotalMessageSent(int totalMessageSent) {
		this.totalMessageSent = totalMessageSent;
	}



	public enum REBmode {
	    ACTIVE, PASSIVE 
	}
}
