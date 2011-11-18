package org.eclipselabs.recommenders.bookmark.tree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class TreeDeSerializer {

	public static TreeNode serializeTree(TreeNode treeRootNode) {

		TreeNode newRootNode = serializeSubTree(treeRootNode);
		
		for (TreeNode rootChilds : newRootNode.getChildren())
			rootChilds.setBookmark(true);

		return newRootNode;

	}

	private static TreeNode serializeSubTree(TreeNode subTreeNode) {

		TreeNode newSubTreeNode = null;

		if (subTreeNode.getValue() instanceof IFile) {
			IFile file = (IFile) subTreeNode.getValue();
			String path = file.getLocationURI().getPath();
			newSubTreeNode = new TreeNode(path);
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

		if (newSubTreeNode == null && !subTreeNode.isBookmarkNode()) {
			try {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IPath location = Path.fromOSString((String) value);
				IFile file = workspace.getRoot().getFileForLocation(location);
				// file.createLink(location, IResource.NONE, null);
				newSubTreeNode = new TreeNode(file);
			} catch (JsonSyntaxException e) {
				// It might not be an Fie
				System.err.println("Json exception");
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
