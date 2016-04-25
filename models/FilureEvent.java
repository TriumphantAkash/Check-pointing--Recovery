package models;

public class FilureEvent {
	private int filureNode;
	private int checkpointCount;
	
	public FilureEvent(int a, int b){
		filureNode = a;
		checkpointCount = b;
	}
	public int getFilureNode() {
		return filureNode;
	}
	public void setFilureNode(int filureNode) {
		this.filureNode = filureNode;
	}
	public int getCheckpointCount() {
		return checkpointCount;
	}
	public void setCheckpointCount(int checkpointCount) {
		this.checkpointCount = checkpointCount;
	}	
}
