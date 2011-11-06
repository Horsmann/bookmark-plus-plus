package org.eclipselabs.recommenders.bookmark.tree.node;

import java.util.ArrayList;

public abstract class TreeNode {
	private TreeNode parent;
	private ArrayList<TreeNode> children;
	private String text;

	public TreeNode(String text) {
		this.text = text;
		children = new ArrayList<TreeNode>();
	}

	public boolean hasParent() {
		return (parent == null);
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

	public String getText() {
		return text;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public TreeNode getParent() {
		return parent;
	}

}
