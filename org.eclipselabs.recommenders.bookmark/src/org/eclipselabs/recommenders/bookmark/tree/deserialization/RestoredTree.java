package org.eclipselabs.recommenders.bookmark.tree.deserialization;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class RestoredTree {
	
	private TreeNode root;
	private TreeNode [] expanded;
	
	public RestoredTree(TreeNode root, TreeNode [] expanded){
		this.root = root;
		this.expanded = expanded;
	}

	public TreeNode getRoot() {
		return root;
	}

	public TreeNode[] getExpanded() {
		return expanded;
	}

}
