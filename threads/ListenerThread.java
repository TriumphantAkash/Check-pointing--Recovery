package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import models.Message;

public class ListenerThread extends Thread{
	private ObjectInputStream ois;
	private BlockingQueue<Message> queue;
	
	public ListenerThread(ObjectInputStream ois, BlockingQueue<Message> queue) {
		this.ois = ois;
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			Message message = (Message)ois.readObject();
			queue.put(message);
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
