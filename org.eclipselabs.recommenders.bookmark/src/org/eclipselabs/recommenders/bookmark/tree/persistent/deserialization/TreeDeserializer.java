package org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.SerializedTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;

public class TreeDeserializer
{

	public static RestoredTree deSerializeTree(String serialized,
			ObjectConverter converter)
	{

		Object[] deserialized = converter.convertToObject(serialized);

		SerializedTreeNode rootNode = (SerializedTreeNode) deserialized[0];

		HashMap<Object, String> expandedNodesMap = new HashMap<Object, String>();

		TreeNode restoredTree = deSerializeTreeValues(rootNode,
				expandedNodesMap);

		RestoredTree rstTree = new RestoredTree(restoredTree, expandedNodesMap
				.keySet().toArray(new TreeNode[0]));

		return rstTree;
	}

	private static TreeNode deSerializeTreeValues(SerializedTreeNode rootNode,
			HashMap<Object, String> expandedNodesMap)
	{

		TreeNode newRootNode = deSeralizeTreeValuesRecursive(rootNode,
				expandedNodesMap);
		newRootNode.setValue("");

		return newRootNode;

	}

	private static TreeNode deSeralizeTreeValuesRecursive(
			SerializedTreeNode node, HashMap<Object, String> expandedNodesMap)
	{

		Object value = node.getValue();
		boolean isBookmark = node.isBookmarkNode();
		boolean isAutoGenerated = node.isAutoGenerated();

		TreeNode newNode = createNodeWithRecoveredValue((String) value,
				isBookmark, isAutoGenerated);

		if (node.isExpanded())
			expandedNodesMap.put(newNode, "");

		for (SerializedTreeNode child : node.getChildren()) {
			TreeNode newChildNode = deSeralizeTreeValuesRecursive(child,
					expandedNodesMap);
			if (newChildNode != null)
				newNode.addChild(newChildNode);
		}

		return newNode;
	}

	private static TreeNode createNodeWithRecoveredValue(String value,
			boolean isBookmark, boolean isAutoGenerated)
	{

		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(value);
		if (element != null) {
			return new TreeNode(element, isBookmark, isAutoGenerated);
		}

		IFile file = TreeValueConverter.attemptTransformationToIFile(value);
		if (file != null) {
			return new TreeNode(file, isBookmark, isAutoGenerated);
		}

		return new TreeNode(value, isBookmark, isAutoGenerated);
	}

}
