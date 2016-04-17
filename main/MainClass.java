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

	private static boolean isOutgoingChannelSetup = false;
	private static BlockingQueue<Message> queue = null;
	
	public static Node thisNode;
	
	public static HashMap<Integer, ObjectOutputStream> neighbourOOS = new HashMap<Integer, ObjectOutputStream>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		thisNode = new Node();
		thisNode.setNodeId(Integer.parseInt(args[0]));
		
		queue = new ArrayBlockingQueue<Message>(1000);
		
		File f = new File(args[1]);
		ConfigParser.readConfig(f);
		
	//	System.out.println("my neighbours: ");
//		for(Integer key: thisNode.getNeighbours().keySet()){
//			System.out.print(key+ "  ");
//		}
		//this node has all the required data in it now
		ServerSocket serverSocket = new ServerSocket(thisNode.getPort());
		System.out.println("Server Started fo node:" +thisNode.getNodeId()+ " With port ID : " +thisNode.getPort());
		
		//optimize this while true
		//because it needs to run only for making new channels
		int incomingChannels = 0;
		for(Integer key: thisNode.getNeighbours().keySet()){
			if(key < thisNode.getNodeId()){
				incomingChannels++;
			}
		}
		
		while((incomingChannels>0) && (thisNode.getNodeId() != 0)){
			//System.out.println("["+thisNode.getNodeId()+"]" +" is waiting at dotAccept");
			Socket sock = serverSocket.accept();
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Message msg = (Message) (ois.readObject());

			//System.out.println("["+thisNode.getNodeId()+"]"+"received"+msg.getMessage()+"from"+msg.getSourceNode().getNodeId());
			
			setupChannel(sock, msg.getSourceNode().getNodeId());
			
			if(isOutgoingChannelSetup == false){
				sendConnectionMsg();
			}
			incomingChannels--;
		}
		
		if(thisNode.getNodeId() == 0){
			//I think here is the problem, 
			//code on Node 0 has to be deployed in the end but looks like it's not the case here
			sendConnectionMsg();
		}
		
		
	serverSocket.close();	
	}
	
	//setup a channel when a new client (lower Node Id) connects to my server Socket
	//when I become client for my neighbours (higher node UD) 
	static void setupChannel(Socket sock, int nodeId) {
		
		//1) store output stream for this channel corresponding to this node id
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("we got some exception in Output stream");
			e.printStackTrace();
		}
		//System.out.println("["+thisNode.getNodeId()+"]"+"got the outputstream");
		neighbourOOS.put(nodeId, oos);
		//System.out.println("["+thisNode.getNodeId()+"]"+"has put the outputstream in hashmap");
		
		//2) create Listener thread for this channel
		ObjectInputStream ois = null;
		try {
			//System.out.println("["+thisNode.getNodeId()+"]"+"input stream with "+nodeId+ " is"+ sock.getInputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			//System.out.println("["+thisNode.getNodeId()+"]"+"got the inutstream");
			//System.out.println("["+thisNode.getNodeId()+"]"+"going to create listener thread"); 
			ListenerThread listenerThread = new ListenerThread(ois, queue);
			listenerThread.start();
			System.out.println("["+thisNode.getNodeId()+"]"+" is listening for incoming msgs from "+ nodeId + "!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("we got some exception in Input stream");
			e.printStackTrace();
		}
		
	}

	
	static void sendConnectionMsg() throws UnknownHostException, IOException, ClassNotFoundException{
		isOutgoingChannelSetup = true;
		Message m = new Message();
		m.setMessage("connection");
		m.setSourceNode(thisNode);
		for(Integer key: thisNode.getNeighbours().keySet()){
			//System.out.println(thisNode.getNodeId()+" took out his neighbour" +key+ "Hostname: " +thisNode.getNeighbours().get(key).getHostName()+ " Port number: " +thisNode.getNeighbours().get(key).getPort());
			if(key > thisNode.getNodeId()){
				m.setDestinationNode(thisNode.getNeighbours().get(key));
				//System.out.println(thisNode.getNodeId()+" is trying to connect to" +key+ "Hostname: " +thisNode.getNeighbours().get(key).getHostName()+ " Port number: " +thisNode.getNeighbours().get(key).getPort());
				Socket sock = new Socket(thisNode.getNeighbours().get(key).getHostName(), thisNode.getNeighbours().get(key).getPort());
				//System.out.println("trying to send the message from " +thisNode.getNodeId()+ "to " +thisNode.getNeighbours().get(key).getNodeId());
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(m);
				//System.out.println("["+thisNode.getNodeId()+"]"+"sending"+m.getMessage()+"to"+m.getDestinationNode().getNodeId());
				setupChannel(sock,key);
			}
		}
	}
}
