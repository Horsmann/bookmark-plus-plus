package org.eclipselabs.recommenders.bookmark;

import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
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

	public static TreeNode getBookmarkNode(TreeNode node) {

		if (node.isBookmarkNode())
			return node;

		return getBookmarkNode(node.getParent());
	}

}
