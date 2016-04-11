package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import main.MainClass;
import models.Node;

public class ConfigParser {
	
	public static void readConfig(File file) throws IOException {
		int nodeNumber = MainClass.thisNode.getNodeId();
		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);
		
		String line = br.readLine();
		String[] words = line.split("\\s+");
		
		MainClass.thisNode.setTotalNodes(Integer.parseInt(words[0]));
		MainClass.thisNode.setTotalFailures(Integer.parseInt(words[1]));
		MainClass.thisNode.setMaxNumber(Integer.parseInt(words[2]));
		MainClass.thisNode.setMaxPerActive(Integer.parseInt(words[3]));

		HashMap<Integer, Node> tempHashMap = new HashMap<Integer, Node>();
		for(int i = 1;i<=MainClass.thisNode.getTotalNodes();i++){
			String line1 = br.readLine();
			String[] words1 = line1.split("\\s+");
			Node n = new Node();
			n.setNodeId(Integer.parseInt(words1[0]));
			n.setHostName(words1[1]);
			n.setPort(Integer.parseInt(words1[2]));;
			tempHashMap.put(n.getNodeId(), n);
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
	}
}
