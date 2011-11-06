package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipselabs.recommenders.bookmark.tree.node.BookmarkNode;
import org.eclipselabs.recommenders.bookmark.tree.node.ReferenceNode;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;


public class TreeModel {

	private TreeNode root = null;

	public TreeModel() {
		root = new ReferenceNode("");
		generateDummyValues();
	}

	public TreeNode getModelRoot() {
		return root;
	}

	public TreeNode generateDummyValues() {

		TreeNode topeNode1 = new BookmarkNode("top", "Schnittstelle zwischen A und B");
		TreeNode level1 = new ReferenceNode("level one");
		level1.addChild(new ReferenceNode("level one one"));
		topeNode1.addChild(level1);

		TreeNode level2 = new ReferenceNode("level two");
		topeNode1.addChild(level2);

		TreeNode level3 = new ReferenceNode("level three");
		topeNode1.addChild(level3);

		TreeNode topeNode2 = new BookmarkNode("top 2", "Code Master-Pieces");
//		TreeNode topeNode2 = new ReferenceNode("top 2");
		TreeNode level11 = new ReferenceNode("level one");
		topeNode2.addChild(level11);

		TreeNode level22 = new ReferenceNode("level two");
		topeNode2.addChild(level22);

		TreeNode level33 = new ReferenceNode("level three");
		topeNode2.addChild(level33);

		root.addChild(topeNode1);
		root.addChild(topeNode2);

		return root;
	}
}
