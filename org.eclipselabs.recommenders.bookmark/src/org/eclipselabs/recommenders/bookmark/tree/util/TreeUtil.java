package org.eclipselabs.recommenders.bookmark.tree.util;

import java.util.LinkedList;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class TreeUtil {

	public static TreeNode getLeafOfTreePath(TreeNode node) {

		if (node == null)
			return null;

		if (node.hasChildren()) {
			TreeNode child = node.getChildren()[0];
			return getLeafOfTreePath(child);
		} else
			return node;
	}

	public static LinkedList<TreeNode> getLeafs(TreeNode node) {

		LinkedList<TreeNode> leafs = new LinkedList<TreeNode>();

		if (node.hasChildren())
			for (TreeNode child : node.getChildren())
				leafs.addAll(getLeafs(child));
		else
			leafs.add(node);
		return leafs;
	}

	/**
	 * Creates a copy of a provided node that includes all leafs, but without
	 * the bookmark nodes the node belongs to. The bookmark node is
	 * <code>null</code> for each copy
	 * 
	 * @param node
	 * @return
	 */
	public static TreeNode copyTreePath(TreeNode node) {

		if (node == null)
			return null;

		LinkedList<TreeNode> newChilds = new LinkedList<TreeNode>();
		for (TreeNode child : node.getChildren())
			newChilds.add(copyTreePathNodeToLeafs(child));

		TreeNode newNode = copyTreePathNodeToBookmark(node);

		if (newNode == null)
			return null;

		for (TreeNode child : newChilds)
			newNode.addChild(child);

		return newNode;

	}

	private static TreeNode copyTreePathNodeToBookmark(TreeNode node) {
		if (node.isBookmarkNode())
			return null;

		TreeNode newNode = new TreeNode(node.getValue());
		TreeNode parent = node.getParent();
		TreeNode newParent = copyTreePathNodeToBookmark(parent);
		if (newParent != null)
			newParent.addChild(newNode);
		return newNode;
	}

	private static TreeNode copyTreePathNodeToLeafs(TreeNode node) {

		Object value = node.getValue();
		TreeNode newNode = new TreeNode(value);

		for (TreeNode child : node.getChildren()) {
			TreeNode newChild = copyTreePathNodeToLeafs(child);
			newNode.addChild(newChild);
		}

		return newNode;
	}

	public static TreeNode getBookmarkNode(TreeNode node) {

		if (node == null)
			return null;

		if (node.isBookmarkNode())
			return node;

		return getBookmarkNode(node.getParent());
	}

	public static TreeNode locateNodeWithEqualID(String id, TreeNode node) {

		if (doesNodeMatchId(id, node))
			return node;
		for (TreeNode child : node.getChildren()) {
			TreeNode located = locateNodeWithEqualID(id, child);
			if (located != null)
				return located;
		}

		return null;
	}

	private static boolean doesNodeMatchId(String id, TreeNode node) {
		Object value = node.getValue();
		if (value == null || id == null)
			return false;
		String compareID = TreeValueConverter.getStringIdentification(value);
		return (id.compareTo(compareID) == 0);
	}

	public static boolean isDuplicate(TreeNode bookmark, TreeNode node) {

		LinkedList<TreeNode> leafs = TreeUtil.getLeafs(node);

		for (TreeNode leaf : leafs) {
			String id = TreeValueConverter.getStringIdentification(leaf
					.getValue());

			boolean duplicateFound = TreeUtil.isDuplicate(bookmark, id);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	public static boolean isDuplicate(TreeNode node, String masterID) {

		String compareID = TreeValueConverter.getStringIdentification(node
				.getValue());
		if (compareID.compareTo(masterID) == 0)
			return true;

		for (TreeNode child : node.getChildren()) {
			boolean duplicateFound = TreeUtil.isDuplicate(child, masterID);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	public static TreeNode climbUpUntilLevelBelowBookmark(TreeNode node) {
		TreeNode climber = node;
		while (climber.getParent() != null
				&& !climber.getParent().isBookmarkNode()) {
			climber = climber.getParent();
		}

		return climber;
	}
}
