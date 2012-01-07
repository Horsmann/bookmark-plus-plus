package org.eclipselabs.recommenders.bookmark.tree.persistent.serialization;

import java.util.HashMap;

import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;

public class TreeSerializer
{

	public static String serializeTree(BMNode root, Object[] expandedNodes,
			ObjectConverter converter)
	{

		HashMap<Object, String> map = putExpandedNodesInHashMap(expandedNodes);

		SerializedTreeNode preSerialized = copyNodeAndSerializeValue(root, map);
		String serialized = converter
				.convertToString(new Object[] { preSerialized });

		return serialized;
	}

	private static HashMap<Object, String> putExpandedNodesInHashMap(
			Object[] expandedNodes)
	{
		HashMap<Object, String> expandedMap = new HashMap<Object, String>();

		if (expandedNodes != null) {
			for (Object o : expandedNodes) {
				expandedMap.put(o, "");
			}
		}
		return expandedMap;
	}

	private static SerializedTreeNode copyNodeAndSerializeValue(BMNode node,
			HashMap<Object, String> expandedMap)
	{

		SerializedTreeNode newNode = null;
		Object value = node.getValue();

		String id = TreeValueConverter.getStringIdentification(value);

		boolean isExpanded = isNodeExpanded(expandedMap, node);
		boolean isBookmark = node.isBookmarkNode();
		boolean showInFlatModus = node.showInFlatModus();

		newNode = new SerializedTreeNode(id, isBookmark, isExpanded,
				showInFlatModus);

		for (BMNode child : node.getChildren()) {
			SerializedTreeNode newChildNode = copyNodeAndSerializeValue(child,
					expandedMap);
			if (newChildNode != null) {
				newNode.addChild(newChildNode);
			}
		}

		return newNode;
	}

	private static boolean isNodeExpanded(HashMap<Object, String> map,
			BMNode node)
	{
		return !(map.get(node) == null);
	}
}
