package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeModel {

	private TreeNode root = null;

	public TreeModel() {
		root = new TreeNode("");
		generateDummyValues();
	}

	public TreeNode getModelRoot() {
		return root;
	}

	public TreeNode generateDummyValues() {

		TreeNode topeNode1 = new TreeNode("top", true);
		TreeNode level1 = new TreeNode("level one");
		topeNode1.addChild(level1);

		TreeNode level2 = new TreeNode("level two");
		topeNode1.addChild(level2);

		TreeNode level3 = new TreeNode("level three");
		topeNode1.addChild(level3);

		TreeNode topeNode2 = new TreeNode("top 2", true);
		TreeNode level11 = new TreeNode("level one");
		topeNode2.addChild(level11);

		TreeNode level22 = new TreeNode("level two");
		topeNode2.addChild(level22);

		TreeNode level33 = new TreeNode("level three");
		topeNode2.addChild(level33);

		root.addChild(topeNode1);
		root.addChild(topeNode2);

		return root;
	}
}
