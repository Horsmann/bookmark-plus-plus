package org.eclipselabs.recommenders.bookmark.tree.deserialization;

import java.util.LinkedList;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.serialization.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.serialization.ObjectConverter;

public class TreeDeserializerFacade {

	public static RestoredTree deserialize(String serializedTree) {
		ObjectConverter converter = new GsonConverter();
		RestoredTree rstTree = TreeDeserializer.deSerializeTree(serializedTree,
				converter);
		
		return rstTree;
	}
	
	public static void setExpandedNodesForView(TreeViewer viewer, TreeNode [] expanded){
		LinkedList<TreeNode> exNodeList = new LinkedList<TreeNode>();
		for (Object node : viewer.getExpandedElements())
			exNodeList.add((TreeNode) node);

		for (TreeNode node : expanded)
			exNodeList.add(node);


		viewer.setExpandedElements(exNodeList.toArray());
	}
	
}
