package main;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import utilities.ConfigParser;
import models.Message;
import models.Node;

public class MainClass {

	public static Node thisNode;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		thisNode = new Node();
		thisNode.setNodeId(Integer.parseInt(args[0]));
		
		File f = new File(args[1]);
		
		ConfigParser.readConfig(f);
		
		//this node has all the required data in it now
		
		
		ServerSocket serverSocket = new ServerSocket(thisNode.getPort());
		
		//optimize this while true
		//because it needs to run only for making new channels
		while(true){
			Socket sock = serverSocket.accept();
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Message msg = (Message) (ois.readObject());
			if(msg.getMessage().equalsIgnoreCase("connection")){
				//store output stream
			}
		}
		
	}

}
