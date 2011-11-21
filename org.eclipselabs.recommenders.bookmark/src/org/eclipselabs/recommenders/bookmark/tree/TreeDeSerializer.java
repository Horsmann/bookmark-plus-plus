package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipselabs.recommenders.bookmark.Util;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

import com.google.gson.JsonSyntaxException;

public class TreeDeSerializer {

	/**
	 * The values of the TreeNodes are transformed to a string representation,
	 * the parent<-->child relationship is reduced to parent-->child (removing
	 * circles)
	 * 
	 * @return
	 */
	public static TreeNode serializeTree(TreeNode treeRootNode) {

		TreeNode newRootNode = serializeSubTree(treeRootNode);

		for (TreeNode rootChilds : newRootNode.getChildren())
			rootChilds.setBookmark(true);

		return newRootNode;

	}

	private static TreeNode serializeSubTree(TreeNode subTreeNode) {

		TreeNode newSubTreeNode = null;
		Object value = subTreeNode.getValue();

		String id = Util.getStringIdentification(value);
		newSubTreeNode = new TreeNode(id);

		for (TreeNode child : subTreeNode.getChildren()) {
			TreeNode newSubTreeChildNode = serializeSubTree(child);
			if (newSubTreeChildNode != null) {
				newSubTreeNode.addChild(newSubTreeChildNode);
				newSubTreeChildNode.setParent(null);
			}
		}

		return newSubTreeNode;
	}

	public static TreeNode deSerializeTree(TreeNode treeRootNode) {

		TreeNode newRootNode = deSerializeSubTree(treeRootNode);
		newRootNode.setValue("");

		return newRootNode;

	}

	private static TreeNode deSerializeSubTree(TreeNode subTreeNode) {

		TreeNode newSubTreeNode = null;

		Object value = subTreeNode.getValue();

		IJavaElement element = JavaCore.create((String) value);

		if (element != null)
			newSubTreeNode = new TreeNode(element);

		if (newSubTreeNode == null && !subTreeNode.isBookmarkNode()) {
			try {
				IPath location = Path.fromOSString((String) value);
				IFile file = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(location);
				newSubTreeNode = new TreeNode(file);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}

		if (newSubTreeNode == null)
			newSubTreeNode = new TreeNode(value, subTreeNode.isBookmarkNode());

		if (newSubTreeNode != null) {
			for (TreeNode child : subTreeNode.getChildren()) {
				TreeNode newSubTreeChildNode = deSerializeSubTree(child);
				if (newSubTreeChildNode != null)
					newSubTreeNode.addChild(newSubTreeChildNode);
			}

		}

		return newSubTreeNode;
	}

}
