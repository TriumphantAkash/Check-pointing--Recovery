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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import utilities.ConfigParser;
import models.Message;
import models.Node;
import models.Node.REBmode;
import threads.ListenerThread;
import threads.WriterThread;

public class MainClass {

	private static boolean isOutgoingChannelSetup = false;
	private static BlockingQueue<Message> LMqueue = null;
	private static BlockingQueue<String> MWqueue = null;
	private static int numberOfSentMessages = 0;
	private static final int MAX_MSG_NUMBER = 10;//later set number of msg sent using configparser
	
	public static Node thisNode;
	private static final Lock _mutex = new ReentrantLock(true);
	
	public static HashMap<Integer, ObjectOutputStream> neighbourOOS = new HashMap<Integer, ObjectOutputStream>();

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		thisNode = new Node();
		thisNode.setNodeId(Integer.parseInt(args[0]));
		
		LMqueue = new ArrayBlockingQueue<Message>(1000);
		MWqueue = new ArrayBlockingQueue<String>(10);
		
		WriterThread writerThread = new WriterThread(MWqueue);
		writerThread.start();
		
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
		serverSocket.close();
		
		if(thisNode.getNodeId() == 0){
			sendConnectionMsg();
			
			_mutex.lock();
			
			System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" got REB mode in "+ MainClass.thisNode.getREBmode());
			if(MainClass.thisNode.getREBmode() == REBmode.PASSIVE){
				//set mode in Message as PASSIVE state
				
				MainClass.thisNode.setREBmode(REBmode.ACTIVE);
				System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"changed REB mode from PASSIVE to ACTIVE (If condition)");
			}else {
				System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" not possible.. something is wrong");
			}
			
			_mutex.unlock();
			
			try {
				MWqueue.put("nod");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//this is node 0 (we are assumig that only node 0 is active in the beginning)
			}
		
		//starting REB protocol i.e. listen at LMqueu and take corresponding action when new message arrives 
		while(true){
			//listen to blocking queue from Listener Thread	
			Message message = LMqueue.take();
			if(message.getRebMode() == Message.REBmode.ACTIVE){
				System.out.println("need to do checkpoint protocol stuff");
				System.out.println("*************************************");
			} else {
				if(numberOfSentMessages < MAX_MSG_NUMBER){
					MWqueue.put("nod");
					numberOfSentMessages++;
				}
				System.out.println("need to do checkpoint protocol stuff");
				System.out.println("*************************************");
			}
		}
		
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
			ListenerThread listenerThread = new ListenerThread(ois, LMqueue);
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
