package org.eclipselabs.recommenders.bookmark.tree.util;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.SerializedTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class TreeDeserializer {


	

	public static RestoredTree deSerializeTree(String serialized, ObjectConverter converter) {
		
		Object [] deserialized = converter.convertToObject(serialized);
		
		SerializedTreeNode rootNode = (SerializedTreeNode) deserialized[0];
		
		HashMap<Object, String> map = new HashMap<Object, String>();
		
		TreeNode restoredTree = deSerializeTreeValues(rootNode, map);
		
		RestoredTree rstTree = new RestoredTree(restoredTree, map.keySet().toArray(new TreeNode [0]));
		
		return rstTree;
	}


	private static TreeNode deSerializeTreeValues(SerializedTreeNode treeRootNode, HashMap<Object, String> map) {

		TreeNode newRootNode = deSeralizeTreeValuesRecursive(treeRootNode, map);
		newRootNode.setValue("");

		return newRootNode;

	}

	private static TreeNode deSeralizeTreeValuesRecursive(SerializedTreeNode subTreeNode, HashMap<Object, String> map) {

		Object value = subTreeNode.getValue();

		TreeNode newSubTreeNode = createNodeWithRecoveredValue((String) value,
				subTreeNode.isBookmarkNode());
		
		if (subTreeNode.isExpanded())
			map.put(newSubTreeNode, "");

		for (SerializedTreeNode child : subTreeNode.getChildren()) {
			TreeNode newSubTreeChildNode = deSeralizeTreeValuesRecursive(child, map);
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
