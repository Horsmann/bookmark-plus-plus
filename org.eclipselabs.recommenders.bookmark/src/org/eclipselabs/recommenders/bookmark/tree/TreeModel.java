package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeModel {

	private TreeNode root = null;

	public TreeModel() {
		root = new TreeNode("");
	}

	public TreeNode getModelRoot() {
		return root;
	}

	public void setRootNode(TreeNode newRoot) {
		root = newRoot;
	}
}
