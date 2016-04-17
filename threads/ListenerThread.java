package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.MainClass;
import models.Message;
import models.Node.REBmode;

public class ListenerThread extends Thread{
	private ObjectInputStream ois;
	private BlockingQueue<Message> queue;
	
	private final Lock _mutex = new ReentrantLock(true);
	
	public ListenerThread(ObjectInputStream ois, BlockingQueue<Message> queue) {
		this.ois = ois;
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			while(true){
				Message message = (Message)ois.readObject();
				if(message.getMessage().equalsIgnoreCase("Application")){
					_mutex.lock();
					System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"got an [Application_msg] from "+ message.getSourceNode().getNodeId());
					System.out.println("["+MainClass.thisNode.getNodeId()+"]"+" got REB mode in "+ MainClass.thisNode.getREBmode());
					if(MainClass.thisNode.getREBmode() == REBmode.PASSIVE){
						//set mode in Message as PASSIVE state
						message.setRebMode(models.Message.REBmode.PASSIVE);
						MainClass.thisNode.setREBmode(REBmode.ACTIVE);
						System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"changed REB mode from PASSIVE to ACTIVE (If condition)");
					}else {
						message.setRebMode(models.Message.REBmode.ACTIVE);
					}
					
					_mutex.unlock();
					
					queue.put(message);
					System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"wrote [Application_msg] from "+ message.getSourceNode().getNodeId()+ "to LMqueue");
				}
			}
			
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
