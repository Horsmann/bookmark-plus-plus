package org.eclipselabs.recommenders.bookmark.tree;

import java.util.ArrayList;

public class SerializedTreeNode extends TreeNode {

	private boolean isExpanded;
	private ArrayList<SerializedTreeNode> serChildren = new ArrayList<SerializedTreeNode>();

	public SerializedTreeNode(Object value, boolean isBookmark,
			boolean isExpanded) {
		super(value, isBookmark);
		this.isExpanded = isExpanded;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public SerializedTreeNode[] getChildren() {
		return (SerializedTreeNode[]) serChildren
				.toArray(new SerializedTreeNode[serChildren.size()]);
	}

	public void addSerChild(SerializedTreeNode child) {
		serChildren.add(child);
	}

	public void removeChild(SerializedTreeNode child) {
		serChildren.remove(child);
		child.setParent(null);
	}
}
