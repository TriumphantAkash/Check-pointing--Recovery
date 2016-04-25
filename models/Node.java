package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import models.Message.REBmode;

public class Node implements Serializable{
	private int nodeId;
	private String hostName;
	private int port;
	private REBmode rebMode = REBmode.PASSIVE;
	private int totalMessageSent;
	private HashMap<Integer, Node> neighbours = new HashMap<Integer, Node>();
	private static int totalNodes;
	private static int totalFailures;
	private static int maxNumber;	//maximum number of message that a node needs to send before becoming permanently passive
	private static int maxPerActive;	//maximum number of neighbours to which messages this node will send
	private HashMap<Integer, Integer> SENT_VECTOR= new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> RCVD_VECTOR= new HashMap<Integer, Integer>();
	
	//Checkpoint vector clock
	private ArrayList<Integer> vectorClock = new ArrayList<Integer>();
	

	
	public synchronized HashMap<Integer, Integer> getSENT_VECTOR() {
		return SENT_VECTOR;
	}

	public synchronized void setSENT_VECTOR(HashMap<Integer, Integer> sENT_VECTOR) {
		SENT_VECTOR = sENT_VECTOR;
	}

	public synchronized HashMap<Integer, Integer> getRCVD_VECTOR() {
		return RCVD_VECTOR;
	}

	public synchronized void setRCVD_VECTOR(HashMap<Integer, Integer> rCVD_VECTOR) {
		RCVD_VECTOR = rCVD_VECTOR;
	}

	public synchronized ArrayList<Integer> getVectorClock() {
		return vectorClock;
	}

	public synchronized void setVectorClock(ArrayList<Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}

	public synchronized int getTotalNodes() {
		return totalNodes;
	}

	public synchronized static void setTotalNodes(int totalNodes) {
		Node.totalNodes = totalNodes;
	}

	public synchronized static int getTotalFailures() {
		return totalFailures;
	}

	public synchronized static void setTotalFailures(int totalFailures) {
		Node.totalFailures = totalFailures;
	}
	
	
		public synchronized static int getMaxNumber() {
			return maxNumber;
		}
	
		public synchronized static void setMaxNumber(int maxNumber) {
			Node.maxNumber = maxNumber;
		}


	public synchronized static int getMaxPerActive() {
		return maxPerActive;
	}

	public synchronized static void setMaxPerActive(int maxPerActive) {
		Node.maxPerActive = maxPerActive;
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
