package com.utd.distributed.process;

public class Process {

    private int id;
    private int root;
    private int[] edgeInfo;
    private int parent;
    private int[] children;
    
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRoot() {
		return root;
	}
	public void setRoot(int root) {
		this.root = root;
	}
	public int[] getEdgeInfo() {
		return edgeInfo;
	}
	public void setEdgeInfo(int[] edgeInfo) {
		this.edgeInfo = edgeInfo;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public int[] getChildren() {
		return children;
	}
	public void setChildren(int[] children) {
		this.children = children;
	}

}
