package org.eclipselabs.recommenders.bookmark.tree.util;

import java.util.HashMap;

import org.eclipselabs.recommenders.bookmark.tree.SerializedTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class TreeSerializer {

	public static String serializeTree(TreeNode root, Object[] expandedNodes,
			ObjectConverter converter) {

		HashMap<Object, String> map = new HashMap<Object, String>();

		for (Object o : expandedNodes)
			map.put(o, "");

		SerializedTreeNode preSerialized = copyTreeAndSerializeTreeValues(root,
				map);
		String serialized = converter.convertToString(new Object[]{preSerialized});

		return serialized;
	}

	private static SerializedTreeNode copyTreeAndSerializeTreeValues(
			TreeNode treeRootNode, HashMap<Object, String> map) {

		SerializedTreeNode newRootNode = copyTreeAndSerializeTreeValuesForAllChildren(
				treeRootNode, map);

		return newRootNode;

	}

	private static SerializedTreeNode copyTreeAndSerializeTreeValuesForAllChildren(
			TreeNode subTreeNode, HashMap<Object, String> map) {

		SerializedTreeNode newSubTreeNode = null;
		Object value = subTreeNode.getValue();

		String id = TreeValueConverter.getStringIdentification(value);

		Object o = map.get(subTreeNode);
		if (o != null)
			newSubTreeNode = new SerializedTreeNode(id,
					subTreeNode.isBookmarkNode(), true);
		else
			newSubTreeNode = new SerializedTreeNode(id,
					subTreeNode.isBookmarkNode(), false);

		for (TreeNode child : subTreeNode.getChildren()) {
			SerializedTreeNode newSubTreeChildNode = copyTreeAndSerializeTreeValuesForAllChildren(
					child, map);
			if (newSubTreeChildNode != null) {
				newSubTreeNode.addSerChild(newSubTreeChildNode);
			}
		}

		return newSubTreeNode;
	}
}
