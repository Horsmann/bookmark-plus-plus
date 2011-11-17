package org.eclipselabs.recommenders.bookmark.tree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class TreeDeSerializer {

	public static TreeNode serializeTree(TreeNode treeRootNode) {

		TreeNode newRootNode = serializeSubTree(treeRootNode);

		return newRootNode;

	}

	private static TreeNode serializeSubTree(TreeNode subTreeNode) {

		TreeNode newSubTreeNode = null;

		if (subTreeNode.getValue() instanceof IFile) {
			Gson gson = new Gson();
			newSubTreeNode = new TreeNode(gson.toJson(subTreeNode.getValue()));
		}

		if (subTreeNode.getValue() instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) subTreeNode.getValue();
			newSubTreeNode = new TreeNode(element.getHandleIdentifier());
		}

		if (subTreeNode.getValue() instanceof String)
			newSubTreeNode = new TreeNode(subTreeNode.getValue());

		for (TreeNode child : subTreeNode.getChildren()) {
			TreeNode newSubTreeChildNode = serializeSubTree(child);
			if (newSubTreeChildNode != null) {
				newSubTreeNode.addChild(newSubTreeChildNode);
				newSubTreeChildNode.setParent(null);
			}
			// linkNodes(newSubTreeNode, newSubTreeChildNode);
		}
		
		if(newSubTreeNode != null)
			newSubTreeNode.setBookmark(true);

		// newSubTreeNode.setValue("DUMMY");
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

		if (newSubTreeNode == null) {
			Gson gson = new Gson();
			try {
				IFile file = gson.fromJson((String) value, IFile.class);
				newSubTreeNode = new TreeNode(file);
			} catch (Exception e) {
				System.err.println("IFile failed");
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
