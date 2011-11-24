package org.eclipselabs.recommenders.bookmark.tree;

import java.util.ArrayList;

public class TreeNode {
	private TreeNode parent;
	private ArrayList<TreeNode> children;
	private Object value;
	
	private boolean isBookmarkNode;

	public TreeNode(Object value) {
		this.value = value;
		children = new ArrayList<TreeNode>();
		isBookmarkNode=false;
	}
	
	public TreeNode(Object value, boolean isRoot) {
		this.value = value;
		children = new ArrayList<TreeNode>();
		this.isBookmarkNode=isRoot;
	}
	
	public boolean hasParent() {
		return (parent != null);
	}
	
	public boolean isBookmarkNode() {
		return isBookmarkNode;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}


	public void addChild(TreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeNode[] getChildren() {
		return (TreeNode[]) children.toArray(new TreeNode[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setBookmark(boolean isBookmark) {
		this.isBookmarkNode = isBookmark;
	}

}
