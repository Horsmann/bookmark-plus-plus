package org.eclipselabs.recommenders.bookmark.tree;


public class TreeModel {

	/**
	 * The root node is the highest node in the hierarchy, below the root
	 * are bookmark nodes
	 */
	private TreeNode root = null;
	
	/**
	 * The head node is the node that is currently set as 'root' for the view
	 */
	private TreeNode head = null;

	public TreeModel() {
		root = new TreeNode("", false, true);
		head = root;
	}
	
	public void setHeadNode(TreeNode node) {
		head = node;
	}
	
	public void resetHeadToRoot(){
		head = root;
	}

	public TreeNode getModelRoot() {
		return root;
	}
	
	public TreeNode getModelHead() {
		return head;
	}
	
	public void setModelRoot(TreeNode root) {
		this.root = root;
		this.head = this.root;
	}
	
	public boolean isHeadEqualRoot() {
		return root == head;
	}
}
