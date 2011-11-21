package org.eclipselabs.recommenders.bookmark;

import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.preferences.formatter.NewLinesTabPage;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class Util {

	public static LinkedList<TreeNode> getLeafs(TreeNode node) {

		LinkedList<TreeNode> leafs = new LinkedList<TreeNode>();

		if (node.hasChildren())
			for (TreeNode child : node.getChildren())
				leafs.addAll(getLeafs(child));
		else
			leafs.add(node);
		return leafs;
	}

	public static String getStringIdentification(Object value) {
		String id = null;
		if (value instanceof IJavaElement)
			id = ((IJavaElement) value).getHandleIdentifier();
		else if (value instanceof IFile) {

			IFile file = (IFile) value;

			id = file.getFullPath()
					.makeRelativeTo(file.getProjectRelativePath()).toOSString();

		} else if (value instanceof String)
			id = (String) value;

		return id;
	}


	/**
	 * Creates a copy of a provided node that includes all leafs, but without the
	 * bookmark nodes the node belongs to. The bookmark node is <code>null</code>
	 * for each copy
	 * @param node
	 * @return
	 */
	public static TreeNode copyTreePathOfLeafExclusiveBookmarkNode(TreeNode node) {

		LinkedList<TreeNode> newChilds = new LinkedList<TreeNode>();
		for (TreeNode child : node.getChildren())
			newChilds.add(copyTreePathNodeToLeafs(child));

		TreeNode newNode = copyTreePathNodeToBookmark(node);

		for(TreeNode child : newChilds)
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

		if (doesNodeMatchesId(id, node))
			return node;
		for (TreeNode child : node.getChildren()) {
			TreeNode located = locateNodeWithEqualID(id, child);
			if (located != null)
				return located;
		}

		return null;
	}

	private static boolean doesNodeMatchesId(String id, TreeNode node) {
		Object value = node.getValue();
		if (value == null || id == null)
			return false;
		String compareID = Util.getStringIdentification(value);
		return (id.compareTo(compareID) == 0);
	}

}
