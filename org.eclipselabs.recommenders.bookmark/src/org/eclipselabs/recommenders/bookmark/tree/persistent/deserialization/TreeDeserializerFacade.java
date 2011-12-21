package org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization;

import java.util.LinkedList;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.ObjectConverter;

public class TreeDeserializerFacade {

	public static RestoredTree deserialize(String serializedTree) {
		ObjectConverter converter = new GsonConverter();
		RestoredTree rstTree = TreeDeserializer.deSerializeTree(serializedTree,
				converter);
		
		return rstTree;
	}
	
	public static void setExpandedNodesForView(TreeViewer viewer, BMNode [] expanded){
		LinkedList<BMNode> exNodeList = new LinkedList<BMNode>();
		for (Object node : viewer.getExpandedElements())
			exNodeList.add((BMNode) node);

		for (BMNode node : expanded)
			exNodeList.add(node);


		viewer.setExpandedElements(exNodeList.toArray());
	}
	
}
