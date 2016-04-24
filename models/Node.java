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
	

	
	public HashMap<Integer, Integer> getSENT_VECTOR() {
		return SENT_VECTOR;
	}

	public void setSENT_VECTOR(HashMap<Integer, Integer> sENT_VECTOR) {
		SENT_VECTOR = sENT_VECTOR;
	}

	public HashMap<Integer, Integer> getRCVD_VECTOR() {
		return RCVD_VECTOR;
	}

	public void setRCVD_VECTOR(HashMap<Integer, Integer> rCVD_VECTOR) {
		RCVD_VECTOR = rCVD_VECTOR;
	}

	public ArrayList<Integer> getVectorClock() {
		return vectorClock;
	}

	public void setVectorClock(ArrayList<Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}

	public int getTotalNodes() {
		return totalNodes;
	}

	public static void setTotalNodes(int totalNodes) {
		Node.totalNodes = totalNodes;
	}

	public static int getTotalFailures() {
		return totalFailures;
	}

	public static void setTotalFailures(int totalFailures) {
		Node.totalFailures = totalFailures;
	}

	public static int getMaxNumber() {
		return maxNumber;
	}

	public static void setMaxNumber(int maxNumber) {
		Node.maxNumber = maxNumber;
	}

	public static int getMaxPerActive() {
		return maxPerActive;
	}

	public static void setMaxPerActive(int maxPerActive) {
		Node.maxPerActive = maxPerActive;
	}
	
	public HashMap<Integer, Node> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(HashMap<Integer, Node> neighbours) {
		this.neighbours = neighbours;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

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
