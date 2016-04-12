package main;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import utilities.ConfigParser;
import models.Message;
import models.Node;
import threads.ListenerThread;

public class MainClass {

	private static boolean channelSetupFlag = false;
	private static BlockingQueue<Message> queue = null;
	
	public static Node thisNode;
	
	public static HashMap<Integer, ObjectOutputStream> neighbourOOS = new HashMap<Integer, ObjectOutputStream>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		thisNode = new Node();
		thisNode.setNodeId(Integer.parseInt(args[0]));
		
		queue = new ArrayBlockingQueue<Message>(1000);
		
		File f = new File(args[1]);
		ConfigParser.readConfig(f);
		
		//this node has all the required data in it now
		ServerSocket serverSocket = new ServerSocket(thisNode.getPort());
		
		//optimize this while true
		//because it needs to run only for making new channels
		int incomingChannels = 0;
		for(Integer key: thisNode.getNeighbours().keySet()){
			if(key < thisNode.getNodeId()){
				incomingChannels++;
			}
		}
		
		while((incomingChannels>0) && thisNode.getNodeId() != 0){
			Socket sock = serverSocket.accept();
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Message msg = (Message) (ois.readObject());
			setupChannel(sock, msg.getSourceNode().getNodeId());
			
			if(channelSetupFlag == false){
				sendConnectionMsg();
			}
			incomingChannels--;
		}
		
		if(thisNode.getNodeId() == 0){
			sendConnectionMsg();
		}
		
		
		
	}
	
	//setup a channel when a new client (lower Node Id) connects to my server Socket
	//when I becomes client for my neighbours (higher node UD) 
	static void setupChannel(Socket sock, int nodeId) throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
		//1) store output stream for this channel corresponding to this node id
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		neighbourOOS.put(nodeId, oos);
		
		//2) create Listener thread for this channel
		ListenerThread listenerThread = new ListenerThread(ois, queue);
		listenerThread.start();
	}

	
	static void sendConnectionMsg() throws UnknownHostException, IOException, ClassNotFoundException{
		channelSetupFlag = true;
		Message m = new Message();
		m.setMessage("connection");
		m.setSourceNode(thisNode);
		for(Integer key: thisNode.getNeighbours().keySet()){
			if(key > thisNode.getNodeId()){
				m.setDestinationNode(thisNode.getNeighbours().get(key));
				Socket sock = new Socket(thisNode.getNeighbours().get(key).getHostName(), thisNode.getNeighbours().get(key).getPort());
				setupChannel(sock, thisNode.getNeighbours().get(key).getNodeId());
			}
		}
	}
}
