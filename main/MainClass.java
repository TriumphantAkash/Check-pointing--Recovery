package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.login.FailedLoginException;

import utilities.ConfigParser;
import models.FilureEvent;
import models.Message;
import models.Node;
import models.Node.REBmode;
import threads.ListenerThread;
import threads.WriterThread;

public class MainClass {

	private static boolean isOutgoingChannelSetup = false;
	private static BlockingQueue<Message> LMqueue = null;
	private static BlockingQueue<String> MWqueue = null;
	public static int numberOfSentMessages = 0;
	private static int totalNodes;
	private static int totalFailures;
	private static int maxNumber;	//maximum number of message that a node needs to send before becoming permanently passive
	private static int maxPerActive;	//maximum number of neighbours to which messages this node will send
	public static ArrayList<FilureEvent> filureEvents = new ArrayList<FilureEvent>();
	
	public static boolean recoveryMode = false;
	public static ArrayList<Integer> rmlist = new ArrayList<Integer>();
	public static int recoveryModeInitiator;
	
	public static ArrayList<FilureEvent> getFilureEvents() {
		return filureEvents;
	}

	public static void setFilureEvents(ArrayList<FilureEvent> filureEvents) {
		MainClass.filureEvents = filureEvents;
	}

	public synchronized static int getNumberOfSentMessages() {
		return numberOfSentMessages;
	}

	public synchronized static void setNumberOfSentMessages(int numberOfSentMessages) {
		MainClass.numberOfSentMessages = numberOfSentMessages;
	}


	public static FileWriter fileWriter;
	
	public static BufferedWriter bw;
	public static PrintWriter out;
	
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
		
		//initialize vector clock
		for(int i=0;i<MainClass.getTotalNodes();i++){
			thisNode.getVectorClock().add(1);
		}
		
		//initialize SEND and RECEIVE vector
		for(Integer key: thisNode.getNeighbours().keySet()){
			thisNode.getSENT_VECTOR().put(key, 0);
			thisNode.getRCVD_VECTOR().put(key, 0);
		}
		
		
		String line = "1-";
		for(Integer value:thisNode.getSENT_VECTOR().values()){
			line=line+value+" ";
		}
		line = line.substring(0, line.length()-1);
		line=line+"-";
		for(Integer value:thisNode.getRCVD_VECTOR().values()){
			line=line+value+" ";
		}
		line = line.substring(0, line.length()-1);
		line=line+"-";
		for(Integer checkpoint:thisNode.getVectorClock()){
			line=line+checkpoint+" ";
		}
		line = line.substring(0, line.length()-1);
		line=line+"-";
		line=line+thisNode.getREBmode();
		
		writeOutput(line, false);
		
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
			
			Thread.sleep(5000); // so that all 
			_mutex.lock();
			
			System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" Main Thread got REB mode in "+ MainClass.thisNode.getREBmode());
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
			System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"Main Thread read [Application_msg] from source"+ message.getSourceNode().getNodeId() + "from LMqueue");
			if(message.getRebMode() == Message.REBmode.ACTIVE){
				
			} else {
				_mutex.lock();
				if(getNumberOfSentMessages() < MainClass.getMaxNumber()){
					
					MWqueue.put("nod");
					//numberOfSentMessages++;
					//System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"number of messages sent by now: "+ numberOfSentMessages + "yo bitches ....");
					
				}
				_mutex.unlock();
				
				System.out.println("need to do checkpoint protocol stuff (else/still need to send msgs)");
				System.out.println("*******************************************************************");
				System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"Main THread wrote [Application_msg] from "+ message.getSourceNode().getNodeId()+ "to MWqueue");
			}
		}
		
		//fileWriter.close();
		
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

	
	static void sendConnectionMsg() throws UnknownHostException, ClassNotFoundException{
		isOutgoingChannelSetup = true;
		Message m = new Message();
		m.setMessage("connection");
		m.setSourceNode(thisNode);
		for(Integer key: thisNode.getNeighbours().keySet()){
			//System.out.println(thisNode.getNodeId()+" took out his neighbour" +key+ "Hostname: " +thisNode.getNeighbours().get(key).getHostName()+ " Port number: " +thisNode.getNeighbours().get(key).getPort());
			if(key > thisNode.getNodeId()){
				m.setDestinationNode(thisNode.getNeighbours().get(key));
				
				//System.out.println(thisNode.getNodeId()+" is trying to connect to" +key+ "Hostname: " +thisNode.getNeighbours().get(key).getHostName()+ " Port number: " +thisNode.getNeighbours().get(key).getPort());
				try{
				Socket sock = new Socket(thisNode.getNeighbours().get(key).getHostName(), thisNode.getNeighbours().get(key).getPort());
				//System.out.println("trying to send the message from " +thisNode.getNodeId()+ "to " +thisNode.getNeighbours().get(key).getNodeId());
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(m);
				//System.out.println("["+thisNode.getNodeId()+"]"+"sending"+m.getMessage()+"to"+m.getDestinationNode().getNodeId());
				setupChannel(sock,key);
			}catch(IOException e){
				System.out.println("exception while ["+thisNode.getNodeId()+"]"+" tries to create socket with "+ key);
			}
			}
		}
	}
	
	public static synchronized void writeOutput(String str, boolean flag){
		//write to file and exit
		File file = new File("Checkpoints_"+thisNode.getNodeId()+".out");
		
		try {
			FileWriter fileWriter = new FileWriter(file, true);
			fileWriter.write(str+"\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static int getTotalNodes() {
		return totalNodes;
	}

	public synchronized static void setTotalNodes(int totalNodes1) {
		totalNodes = totalNodes1;
	}

	public synchronized static int getTotalFailures() {
		return totalFailures;
	}

	public synchronized static void setTotalFailures(int totalFailures1) {
		totalFailures = totalFailures1;
	}
	
	
		public synchronized static int getMaxNumber() {
			return maxNumber;
		}
	
		public synchronized static void setMaxNumber(int maxNumber1) {
			maxNumber = maxNumber1;
		}


	public synchronized static int getMaxPerActive() {
		return maxPerActive;
	}

	public synchronized static void setMaxPerActive(int maxPerActive1) {
		maxPerActive = maxPerActive1;
	}
	
}
