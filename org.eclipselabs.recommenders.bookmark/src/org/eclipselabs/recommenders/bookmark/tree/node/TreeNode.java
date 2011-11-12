package org.eclipselabs.recommenders.bookmark.tree.node;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;

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
	
	public Object getValue() {
		return value;
	}

	public void removeAllChildren() {
		children = new ArrayList<TreeNode>();
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

	public void setText(Object value) {
		this.value = value;
	}

	public Object getText() {
		return value;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public TreeNode getParent() {
		return parent;
	}

//	public String getProject() {
//		int posFirstSlash = text.indexOf("/");
//		int posSecondSlash = 0;
//		if (posFirstSlash > -1)
//			posSecondSlash = text.indexOf("/", posFirstSlash + 1);
//		else
//			return text;
//
//		if (posSecondSlash > -1) {
//			return text.substring(posFirstSlash + 1, posSecondSlash
//					- posFirstSlash);
//		}
//		return text;
//	}
//
//	public String getName() {
//		int posLastSlash = text.lastIndexOf("/");
//		int posDot = text.lastIndexOf(".");
//
//		if (posDot == -1 || posLastSlash == -1)
//			return text;
//
//		String name = text.substring(posLastSlash + 1);
//
//		return name;
//	}

}
