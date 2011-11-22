package org.eclipselabs.recommenders.bookmark.tree;

import java.lang.reflect.Type;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TreeDeSerializer {

	public static String serializeTreeToGson(TreeNode root) {

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

	private static TreeNode copyTreeAndSerializeTreeValuesForAllChildren(
			TreeNode subTreeNode) {

		TreeNode newSubTreeNode = null;
		Object value = subTreeNode.getValue();

		String id = TreeValueConverter.getStringIdentification(value);
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

	public static TreeNode deSerializeTreeFromGSonString(String gsonSerialized) {
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		TreeNode rootNode = gson.fromJson(gsonSerialized, typeOfSrc);

		TreeNode deSerializedTreesRoot = TreeDeSerializer
				.deSerializeTreeValues(rootNode);
		return deSerializedTreesRoot;
	}

	private static TreeNode deSerializeTreeValues(TreeNode treeRootNode) {

		TreeNode newRootNode = deSeralizeTreeValuesRecursive(treeRootNode);
		newRootNode.setValue("");

		return newRootNode;

	}

	private static TreeNode deSeralizeTreeValuesRecursive(TreeNode subTreeNode) {

		Object value = subTreeNode.getValue();

		TreeNode newSubTreeNode = createNodeWithRecoveredValue((String) value,
				subTreeNode.isBookmarkNode());

		for (TreeNode child : subTreeNode.getChildren()) {
			TreeNode newSubTreeChildNode = deSeralizeTreeValuesRecursive(child);
			if (newSubTreeChildNode != null)
				newSubTreeNode.addChild(newSubTreeChildNode);
		}

		return newSubTreeNode;
	}

	private static TreeNode createNodeWithRecoveredValue(String value,
			boolean isBookmark) {
		
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(value);
		if (element != null)
			return new TreeNode(element, isBookmark);

		
		IFile file = TreeValueConverter.attemptTransformationToIFile(value);
		if (file != null)
			return new TreeNode(file, isBookmark);

		

		return new TreeNode(value, isBookmark);
	}

}
