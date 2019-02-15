package com.utd.distributed.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.utd.distributed.master.Master;
import com.utd.distributed.util.Message;

public class Process implements Runnable{

	private Master master;
	private int root;
    private int id;
    private int[] neighbors;
    private Process[] p;
    private int parent;
    private volatile String messageFromMaster = "";
    private int round = 0;
	private boolean processDone = false;
	private HashMap<Integer,String> status = new HashMap<Integer,String>();
	
	private volatile Queue<Message> messageQueue = new LinkedList<>();
	private volatile boolean marked = false;
    
	public Process(int id, int root,int[] neighbors,Master master) {
		this.id = id;
		this.root = root;
		this.neighbors = neighbors;
		this.master = master;
		for(Integer a : neighbors){
			
			status.put(a, "Unknown");
		}
	}
    
	public void setProcessNeighbors(Process p[]) {
		this.p = p;
	}

	@Override
	public void run() {
		System.out.println("Process " + id + " started");
		// TODO Auto-generated method stub
		master.roundCompletionForProcess(id);
		System.out.println("Process "+id+" ready");
		while(!processDone){
			System.out.print(" Message is "+getMessageFromMaster()+"     ");
			if(getMessageFromMaster().equals("Initiate")){
				System.out.println("Process "+id+" entered initiate");
				setMessageFromMaster("");
				master.roundCompletionForProcess(id);	
			}else if (getMessageFromMaster().equals("StartRound")){
				
				round++;
				System.out.println("Process "+id+" entered start round");
				setMessageFromMaster("");
				boolean visited = receiveMessages();
				System.out.println("If process:" + id + "is marked :"+visited);
				if((id == root && round == 1) || visited) {
					sendMessages();
				}
				
				p[id].modifyQueue(null, this.id, "reset");
				master.roundCompletionForProcess(id);
			}else if(getMessageFromMaster().equals("SendParent")){
								
				System.out.println("Process "+id+" entered send parent");
				setMessageFromMaster("");
				master.assignParents(id,parent);
				master.roundCompletionForProcess(id);
			}else if(getMessageFromMaster().equals("MasterDone")){
				// Terminating the Algorithm
				System.out.println("Process: " + id + "; Parent: " + parent );
				processDone = true;
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
		System.out.print("Entered setMessageFromMaster and msg of "+this.id +" is "+this.messageFromMaster+" ");
	}
	
	// Sending current distance to all neighbors
		private void sendMessages(){
			Message message = new Message();
			message.setFromId(this.id);
			for (int n : neighbors) {
				System.out.println(n);
				message.setSentRound(round);
				p[n].modifyQueue(message, this.id, "insert");

			}
		}
		
		// Processing messages in the Queue 
		private boolean receiveMessages(){
			
			Message msg = modifyQueue(null, -1, "poll");
			while(msg != null) {
				if(this.marked == false) {
					System.out.println("message not null for ID" + id);
					this.parent = msg.getFromId();
					System.out.println("Parent of "+ id + " is :" + parent);
					p[id].acknowledgeStatus(id, "Unknown", true);
					this.marked = true;
				}else {
					p[msg.getFromId()].acknowledgeStatus(id, "Reject", false);
				}
			}
			if (acknowledge(id)) {
				System.out.print("Acknowledged process is "+id+" parent is "+parent);
				if(parent == id){
					Master.treeDone = true;
				}
				else {
					p[parent].acknowledgeStatus(id, "Done", false);
				}
			}
			return marked;
		}

		
		/*
		 * If for a particular process if it's unable to relax other process weights then it sends I'm done to parent.
		 * If source recieves I'm done from all it's neighbours it terminates algorithm by updating treeDone of master
		 * */
		public synchronized boolean acknowledge(int id){
			for (Map.Entry<Integer, String> m : status.entrySet()) {
				if (m.getKey() != parent && m.getValue().equals("Unmarked")) {
					return false;
				}
			}
			return true;
		}
		
		
		/*
		 * Checks whether a process still need to send explore message or not
		 * */
		public synchronized boolean acknowledgeStatus(int id, String reply, boolean reset){
			if(reset){
				for(Integer val : neighbors){
					status.put(val,"Unmarked");
				}
				return true;
			}else{
				status.put(id, reply);
			}
			return true;
		}
		
		/*
		 * Insert, Poll or reset the message Queue
		 * */
		public synchronized Message modifyQueue(Message msg, int fromid, String action){
			
			if("insert".equalsIgnoreCase(action)) {
				messageQueue.add(msg);
			}
			else if("poll".equalsIgnoreCase(action)) {
				if (!messageQueue.isEmpty())
					return messageQueue.peek();
			}
			else if("reset".equalsIgnoreCase(action)) {
				while(!messageQueue.isEmpty()) ;
			}
			return null;
		}
}
