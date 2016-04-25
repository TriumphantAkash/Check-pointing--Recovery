package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import main.MainClass;
import models.FilureEvent;
import models.Node;

public class ConfigParser {
	
	public static void readConfig(File file) throws IOException {
		int nodeNumber = MainClass.thisNode.getNodeId();
		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);
		
		String line = br.readLine();
		String[] words = line.split("\\s+");
		
		MainClass.setTotalNodes(Integer.parseInt(words[0]));
		MainClass.setTotalFailures(Integer.parseInt(words[1]));
		MainClass.setMaxNumber(Integer.parseInt(words[2]));
		MainClass.setMaxPerActive(Integer.parseInt(words[3]));

		HashMap<Integer, Node> tempHashMap = new HashMap<Integer, Node>();
		for(int i = 1;i<=MainClass.getTotalNodes(); i++){
			String line1 = br.readLine();
			String[] words1 = line1.split("\\s+");
			
			if(Integer.parseInt(words1[0]) == MainClass.thisNode.getNodeId()){
				MainClass.thisNode.setHostName(words1[1]);
				MainClass.thisNode.setPort(Integer.parseInt(words1[2]));
			}else{
			
			Node n = new Node();
			n.setNodeId(Integer.parseInt(words1[0]));
			n.setHostName(words1[1]);
			n.setPort(Integer.parseInt(words1[2]));;
			tempHashMap.put(n.getNodeId(), n);
			}
		}
		
		for(Entry<Integer, Node> e: tempHashMap.entrySet()){
			//System.out.println("["+MainClass.thisNode.getNodeId()+"]"+"tempHashMap entries: " + e.getKey()+ "and "+e.getValue().getHostName()+ " "+e.getValue().getPort());
		}
		
		
		for(int i = 1; i <=MainClass.thisNode.getNodeId(); i++){
			String line2 = br.readLine();
		}
		
		//now the next line will read the neighbours of current node
		String line3 = br.readLine();
		String[] words3 = line3.split("\\s+");
		
		for(int i = 0; i< words3.length; i++){
			MainClass.thisNode.getNeighbours().put(Integer.parseInt(words3[i]) ,tempHashMap.get(Integer.parseInt(words3[i])));
		}
		//now my neighbour's information is updated
		
		//there is some more stuff need to be parsed while doing part 3 of this project
		for(int i = 1; i <=(MainClass.getTotalNodes() - MainClass.thisNode.getNodeId() - 1); i++){
			String line4 = br.readLine();
		}
		
		for(int i=0; i<MainClass.getTotalFailures(); i++){
			String event = br.readLine();
			String[] events = event.split("\\s+");
			MainClass.getFilureEvents().add(new FilureEvent(Integer.parseInt(events[0]), Integer.parseInt(events[0])));
		}
	}
}
