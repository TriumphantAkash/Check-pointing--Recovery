package threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.MainClass;
import models.Message;
import models.Node.REBmode;

public class WriterThread extends Thread{
	private final Lock _mutex = new ReentrantLock(true);
	private BlockingQueue<String> queue;
	public WriterThread(BlockingQueue<String> queue) {
		this.queue = queue;
	}
	@Override
	public void run() {
		
		while(true){
			try {
				String str = queue.take();
				System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"Writer Thread read got a 'nod' from MainTHread in its MWqueue");
				
				int r = 0;
				if(str.equals("nod")){
					//1) generate random number to pick number of neighbours to send application msg to (suppose this ransom # is r)
					int n = MainClass.thisNode.getNeighbours().size();
					Random rand = new Random();
					r = 1 + rand.nextInt(n - 1 + 1);
					System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" in WriteThread, got generated value of r as: "+ r);
					
					//2) select r neighbours from the neighbour list randomly and send application msg to them
					ArrayList<Integer> list = new ArrayList<Integer>();
					
					for(Integer i: MainClass.thisNode.getNeighbours().keySet()){
						list.add(i);
					}
					
					Collections.shuffle(list);
					
					//create application message
					Message msg = new Message();
					msg.setSourceNode(MainClass.thisNode);
					msg.setMessage("Application");
					
					//take first r nodeIds from list and send application msg to them					
					for(int i = 0; i<r;i++){
						int nodeId = list.get(i);
						System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" in Writer Thrad, sending [Application_msg] to : "+ nodeId);
						//System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" in Writer Thread, the output stream for neighbour : "+ nodeId + "is : ***********"+ MainClass.neighbourOOS.get(nodeId));
						MainClass.neighbourOOS.get(nodeId).writeObject(msg);
						Thread.sleep(10);
					}
					
					_mutex.lock();
					
					System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" got REB mode in "+ MainClass.thisNode.getREBmode());
					if(MainClass.thisNode.getREBmode() == REBmode.ACTIVE){
						//set mode in Message as PASSIVE state
						MainClass.thisNode.setREBmode(REBmode.PASSIVE);
						System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"changed REB mode from ACTIVE to PASSIVE (If condition, WriterThread)");
					}else {
						System.out.println("Something fishy going on baby...");
					}
					_mutex.unlock();
					
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
