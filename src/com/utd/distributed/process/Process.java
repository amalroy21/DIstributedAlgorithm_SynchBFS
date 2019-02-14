package com.utd.distributed.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.utd.distributed.master.Master;
import com.utd.distributed.util.Message;

public class Process implements Runnable{

	private Master master;
    private int id;
    private int root;
    private int[] neighbors;
    private Process[] p;
    private int parent;
    private int[] children;
    private volatile String messageFromMaster = "";
    private int round = 0;
	private boolean processDone = false;
	private HashMap<Integer,String> status = new HashMap<Integer,String>();
	private volatile HashMap<Integer, Queue<Message>> link = new HashMap<>();
	private volatile Queue<Message> messageQueue = new LinkedList<>();
    
	public Process(int id, int root,int[] neighbors,Master master) {
		this.id = id;
		this.root = root;
		this.neighbors = neighbors;
		this.master = master;
		for(Integer a : neighbors){
			//System.out.print(" "+a);
			status.put(a, "Unknown");
			Queue<Message> queue = new LinkedList<>();
			link.put(a, queue);
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
				/*
				 * Messages in the Queue are processed based on FIFO and then
				 * the current distance is sent to all the neighbors
				 */
				round++;
				System.out.println("Process "+id+" entered start round");
				setMessageFromMaster("");
				boolean change = receiveMessages();
				//System.out.println("Change is "+change);
				if(change){
					sendMessages();
				}
				master.roundCompletionForProcess(id);
			}else if(getMessageFromMaster().equals("SendParent")){
				/* Master process collects parents
				 */
				
				//System.out.println("Process "+id+" entered send parent");
				setMessageFromMaster("");
				int weight;
				
				master.assignParents(id,parent);
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
			for (int n : neighbors) {
				System.out.println(n);
				//message.setTransmissionTime(random.nextInt(18));
				message.setSentRound(round);
				p[n].modifyQueue(message, true, this.id, "insert");

			}
		}
		
		// Processing messages in the Queue 
		private boolean receiveMessages(){
			boolean flag = false;
			for (Integer neighbor : link.keySet()) {
				//System.out.println(neighbor);
				Message msg = modifyQueue(null, false, neighbor, "poll");
				//System.out.println(msg);
				while(msg != null){
					System.out.println("message not null");
						if(newDistance < distance){	// Relaxation
							flag = true;
							this.distance = newDistance;
							if(parent != -1){
								p[parent].acknowledgeStatus(id,"Reject",false);
							}
							this.parent = msg.getFromId();
							p[id].acknowledgeStatus(id, "Unknown", true);
						}else{
							p[msg.getFromId()].acknowledgeStatus(id, "Reject", false);
						}
					msg = new Message();
					msg = modifyQueue(null, false, neighbor, "insert");
				}
				if (acknowledge(id)) {
					//System.out.print("Acknowledged process is "+id+" parent is "+parent);
					if(parent == id){
						Master.treeDone = true;
					}
					else {
						p[parent].acknowledgeStatus(id, "Done", false);
					}
				}
			}
			
			return flag;
		}
		
		/*
		 * If for a particular process if it's unable to relax other process weights then it sends I'm done to parent.
		 * If source recieves I'm done from all it's neighbours it terminates algorithm by updating treeDone of master
		 * */
		public synchronized boolean acknowledge(int id){
			for (Map.Entry<Integer, String> m : status.entrySet()) {
				if (m.getKey() != parent && m.getValue().equals("Unknown")) {
					return false;
				}
			}
			return true;
		}
		
		
		/*
		 * Checks whether a process still need to send explore message or not
		 * */
		public synchronized boolean acknowledgeStatus(int nid, String reply, boolean reset){
			if(reset){
				for(Integer val : neighbors){
					status.put(val,"Unknown");
				}
				return true;
			}else{
				status.put(nid, reply);
			}
			return true;
		}
		
		
		public synchronized Message modifyQueue(Message msg, boolean insert, int fromid, String action){
			
			if("insert".equalsIgnoreCase(action)) {
				messageQueue.add(msg);
			}
			else if("poll".equalsIgnoreCase(action)) {
				if (!messageQueue.isEmpty())
					return messageQueue.peek();
			}
			return null;
		}
}
