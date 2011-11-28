package org.eclipselabs.recommenders.bookmark.tree.persistent.serialization;

import java.util.ArrayList;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class SerializedTreeNode {

	private boolean isExpanded;
	private ArrayList<SerializedTreeNode> children;
	private Object value;
	private boolean isBookmarkNode;

	public SerializedTreeNode(Object value, boolean isBookmark,
			boolean isExpanded) {
		this.value = value;
		this.isBookmarkNode = isBookmark;
		this.isExpanded = isExpanded;
		children = new ArrayList<SerializedTreeNode>();
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public boolean isBookmarkNode() {
		return isBookmarkNode;
	}

	public Object getValue() {
		return value;
	}

	public void addChild(SerializedTreeNode child) {
		children.add(child);
	}

	public void removeChild(TreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public SerializedTreeNode[] getChildren() {
		return (SerializedTreeNode[]) children
				.toArray(new SerializedTreeNode[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
}
