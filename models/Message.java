package models;

import java.io.Serializable;

public class Message implements Serializable{

		private String message;
		private Node sourceNode;
		private Node destinationNode;
		private REBmode rebMode;
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public Node getSourceNode() {
			return sourceNode;
		}
		public void setSourceNode(Node sourceNode) {
			this.sourceNode = sourceNode;
		}
		public Node getDestinationNode() {
			return destinationNode;
		}
		public void setDestinationNode(Node destinationNode) {
			this.destinationNode = destinationNode;
		}
		
		public REBmode getRebMode() {
			return rebMode;
		}
		public void setRebMode(REBmode rebMode) {
			this.rebMode = rebMode;
		}



		public enum REBmode {
		    ACTIVE, PASSIVE 
		}
}
