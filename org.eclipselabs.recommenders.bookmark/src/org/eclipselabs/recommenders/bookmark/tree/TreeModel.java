package org.eclipselabs.recommenders.bookmark.tree;


public class TreeModel {

	private TreeNode root = null;

	public TreeModel() {
		root = new TreeNode("");
	}

	public TreeNode getModelRoot() {
		return root;
	}
	
	public void setModelRoot(TreeNode root) {
		this.root = root;
	}
}
