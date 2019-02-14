/**
 *
 * Main.java - Entry point for Distributed Algorithm for SynchBFS
 *
 * @args: <configuration filepath> <server or client> <server or client ID>
 *
 */

package com.utd.distributed;

import com.utd.distributed.master.Master;
import com.utd.distributed.process.Node;
import com.utd.distributed.util.ReadPropertyFile;

import java.util.Properties;

public class Main {
	
	public static Node[] p;
	public static int nodeCount;
	public static int source;
	
    public static void main(String[] args) {

        try {
            String filePath = "config.properties";
            Properties prop = ReadPropertyFile.readProperties(filePath);
            
            
            nodeCount = Integer.parseInt(prop.getProperty("numberofProcess"));
            p = new Node[nodeCount];
			Master master = new Master(nodeCount);
			String[] edgeList = prop.getProperty("edgeList").split(",");
			int[][] neighbors = new int[nodeCount][nodeCount];
			int root = Integer.parseInt(prop.getProperty("root"));
			System.out.println("Neighbours->");
			for(int i = 0; i < nodeCount; i++){
				for(int j = 0; j < nodeCount; j++){
					neighbors[i][j] = Integer.parseInt(edgeList[i].substring(j, j+1));
					System.out.print(neighbors[i][j]);
				}
				p[i] = new Node(i,root,neighbors[i],master);
			}
			
			master.setProcesses(p);
			Thread t = new Thread(master);
			t.start();
			for(int i = 0; i < nodeCount; i++){
				Thread tempThread = new Thread(p[i]);
				tempThread.start();
			}
        }
        catch (Exception ex)    {
            ex.printStackTrace();
        }
    }
}
