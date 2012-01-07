package org.eclipselabs.recommenders.bookmark.tree.persistent.serialization;

import java.util.ArrayList;

public class SerializedTreeNode
{

	private boolean isExpanded;
	private ArrayList<SerializedTreeNode> children;
	private Object value;
	private boolean isBookmarkNode;
	private boolean showInFlatModus;

	public SerializedTreeNode(Object value, boolean isBookmark,
			boolean isExpanded, boolean showInFlatModus)
	{
		this.value = value;
		this.isBookmarkNode = isBookmark;
		this.isExpanded = isExpanded;
		this.showInFlatModus = showInFlatModus;
		children = new ArrayList<SerializedTreeNode>();
	}

	public boolean isExpanded()
	{
		return isExpanded;
	}

	public boolean isBookmarkNode()
	{
		return isBookmarkNode;
	}

	public boolean showInFlatModus()
	{
		return showInFlatModus;
	}

	public Object getValue()
	{
		return value;
	}

	public void addChild(SerializedTreeNode child)
	{
		children.add(child);
	}

	public SerializedTreeNode[] getChildren()
	{
		return (SerializedTreeNode[]) children
				.toArray(new SerializedTreeNode[children.size()]);
	}

}
