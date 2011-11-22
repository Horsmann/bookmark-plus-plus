package org.eclipselabs.recommenders.bookmark.tree;

import java.lang.reflect.Type;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class TreeDeSerializer {
	
	
	public static String serializeTreeToGson(TreeNode root){
		
		TreeNode preSerialized = copyTreeAndSerializeTreeValues(root);
		
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		String gsonTreeString = gson.toJson(preSerialized, typeOfSrc);
		
		return gsonTreeString;
	}

	
	
	/**
	 * The values of the TreeNodes are transformed to a string representation,
	 * the parent<-->child relationship is reduced to parent-->child (removing
	 * circles)
	 * 
	 * @return
	 */
	private static TreeNode copyTreeAndSerializeTreeValues(TreeNode treeRootNode) {

		TreeNode newRootNode = copyTreeAndSerializeTreeValuesForAllChildren(treeRootNode);

		for (TreeNode rootChilds : newRootNode.getChildren())
			rootChilds.setBookmark(true);

		return newRootNode;

	}

	private static TreeNode copyTreeAndSerializeTreeValuesForAllChildren(TreeNode subTreeNode) {

		TreeNode newSubTreeNode = null;
		Object value = subTreeNode.getValue();

		String id = TreeUtil.getStringIdentification(value);
		newSubTreeNode = new TreeNode(id);

		for (TreeNode child : subTreeNode.getChildren()) {
			TreeNode newSubTreeChildNode = copyTreeAndSerializeTreeValuesForAllChildren(child);
			if (newSubTreeChildNode != null) {
				newSubTreeNode.addChild(newSubTreeChildNode);
				newSubTreeChildNode.setParent(null);
			}
		}

		return newSubTreeNode;
	}
	
	public static TreeNode deSerializeTreeFromGSonString(String gsonSerialized){
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		TreeNode rootNode = gson.fromJson(gsonSerialized, typeOfSrc);

		TreeNode deSerializedTreesRoot = TreeDeSerializer.deSerializeTreeValues(rootNode);
		return deSerializedTreesRoot;
	}
	
	private static TreeNode deSerializeTreeValues(TreeNode treeRootNode) {

		TreeNode newRootNode = deSeralizeTreeValuesForChildren(treeRootNode);
		newRootNode.setValue("");

		return newRootNode;

	}

	private static TreeNode deSeralizeTreeValuesForChildren(TreeNode subTreeNode) {

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
				TreeNode newSubTreeChildNode = deSeralizeTreeValuesForChildren(child);
				if (newSubTreeChildNode != null)
					newSubTreeNode.addChild(newSubTreeChildNode);
			}

		}

		return newSubTreeNode;
	}

}
