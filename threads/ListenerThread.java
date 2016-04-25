package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
					
					////////////////////////////////
					//update RCVD_VECTOR value with corresponding node id
					int cur = MainClass.thisNode.getRCVD_VECTOR().get(message.getSourceNode().getNodeId());
					cur++;
					MainClass.thisNode.getRCVD_VECTOR().put(message.getSourceNode().getNodeId(), cur);
					
					
					//update my check-pointing vector clock after receiving message
					
					ArrayList<Integer> piggyBackedClock = message.getSourceNode().getVectorClock();

					for(int i=0; i<MainClass.getTotalNodes();i++){
						int a = piggyBackedClock.get(i);
						int b = MainClass.thisNode.getVectorClock().get(i);
						if(a > b){
							MainClass.thisNode.getVectorClock().set(i, a);
						}
					}
					//update my check-pointing vector clock after receiving message
					int cur_vc = MainClass.thisNode.getVectorClock().get(MainClass.thisNode.getNodeId());
					cur_vc++;
					MainClass.thisNode.getVectorClock().set(MainClass.thisNode.getNodeId(), cur_vc);
					
					
					//take checkpoint
					String line=cur_vc+"-";
					for(Integer value:MainClass.thisNode.getSENT_VECTOR().values()){
						line=line+value+" ";
					}
					line = line.substring(0, line.length()-1);
					line=line+"-";
					for(Integer value:MainClass.thisNode.getRCVD_VECTOR().values()){
						line=line+value+" ";
					}
					line = line.substring(0, line.length()-1);
					line=line+"-";
					for(Integer checkpoint:MainClass.thisNode.getVectorClock()){
						line=line+checkpoint+" ";
					}
					line = line.substring(0, line.length()-1);
					line=line+"-";
					line=line+MainClass.thisNode.getREBmode();
					
					//TESTING PURPOSE
					line = line+"_MSG_RCV_CHECKPOINT";
					/////////////
					
					MainClass.writeOutput(line, false);
					/////////////////////////////////
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
