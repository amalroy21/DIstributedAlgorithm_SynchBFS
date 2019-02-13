package com.utd.distributed.process;

import com.utd.distributed.master.Master;
import com.utd.distributed.util.Message;

public class Process implements Runnable{

	private Master master;
    private int id;
    private int root;
    private int[] edgeInfo;
    private Process[] p;
    private int parent;
    private int[] children;
    private volatile String messageFromMaster = "";
    private int round = 0;
	private boolean processDone = false;
    
	public Process(int id, int root,int[] edgeInfo) {
		this.id = id;
		this.root = root;
		this.edgeInfo=edgeInfo;
	}
    
	public void setProcessNeighbors(Process p[]) {
		this.p = p;
	}

	@Override
	public void run() {
		//System.out.println("Process " + id + " started");
		// TODO Auto-generated method stub
		master.roundCompletionForProcess(id);
		//System.out.println("Process "+id+" ready");
		while(!processDone){
			//System.out.print(" Mesg is "+getMessageFromMaster()+"     ");
			if(getMessageFromMaster().equals("Initiate")){
				//System.out.println("Process "+id+" entered initiate");
				setMessageFromMaster("");
				//System.out.println("distance is "+distance);
				//if(distance == 0){
					//System.out.println("in distance=0");
					sendMessages();
				//}
				master.roundCompletionForProcess(id);	
			}else if (getMessageFromMaster().equals("StartRound")){
				/*
				 * Messages in the Queue are processed based on FIFO and then
				 * the current distance is sent to all the neighbors
				 */
				round++;
				//System.out.println("Process "+id+" entered start round");
				setMessageFromMaster("");
				//boolean change = receiveMessages();
				//System.out.println("Change is "+change);
				//if(change){
					sendMessages();
				//}
				master.roundCompletionForProcess(id);
			}else if(getMessageFromMaster().equals("SendParent")){
				/* Master process collects parents
				 */
				
				//System.out.println("Process "+id+" entered send parent");
				setMessageFromMaster("");
				int weight;
				if(id == parent)
					weight = 0;
				else{
					//System.out.println("parent is "+parent);
					//weight = neighborsAndDistance.get(parent);
				}
				//master.assignParents(id,parent,weight);
				master.roundCompletionForProcess(id);
			}else if(getMessageFromMaster().equals("MasterDone")){
				// Terminating the Algorithm
				//System.out.println("Process: " + id + "; Parent: " + parent + "; Distance from Source: " + distance);
				processDone = true;
			}else{
				//System.out.print("Entered else");
			}
		}
	}
	
	/* To get the recent signal send by the master process. 
	 * Inorder to execute the distributed systems synchronously
	 * */
	public String getMessageFromMaster(){
		return messageFromMaster;
	}
	
	/*
	 * To set the signal by the master process to processes. 
	 * Inorder to execute the distributed systems synchronously
	 * */
	public void setMessageFromMaster(String messageFromMaster) {
		this.messageFromMaster = messageFromMaster;
		//System.out.print("Entered setMessageFromMaster and msg of "+this.id +" is "+this.messageFromMaster+" ");
	}
	
	// Sending current distance to all neighbors
		private void sendMessages(){
			Message message = new Message();
			message.setFromId(this.id);
			//message.setCurrentDistance(this.distance);
			//for (int n : neighborsAndDistance.keySet()) {
				//System.out.println(n);
				//message.setTransmissionTime(random.nextInt(18));
				message.setSentRound(round);
				//p[n].modifyQueue(message, true, this.id, false);

			//}
		}

}
