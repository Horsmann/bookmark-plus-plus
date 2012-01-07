package org.eclipselabs.recommenders.bookmark.tree;

import java.util.ArrayList;

import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class TreeNode
	implements BMNode
{
	private BMNode parent;
	private ArrayList<BMNode> children;
	private Object value;

	private boolean isBookmarkNode;

	/**
	 * Describes whether a node was created automatically for building up the
	 * hierarchy for another node or whether it was dropped by the user
	 */
	private boolean showInFlatModus;

	public TreeNode(Object value, boolean isBookmarkNode,
			boolean showInFlatModus)
	{
		this.value = value;
		this.children = new ArrayList<BMNode>();
		this.isBookmarkNode = isBookmarkNode;
		this.showInFlatModus = showInFlatModus;
	}

	@Override
	public boolean hasParent()
	{
		return (parent != null);
	}

	@Override
	public boolean showInFlatModus()
	{
		return showInFlatModus;
	}
	
	@Override
	public void setShowInFlatModus(boolean state) {
		showInFlatModus = state;
	}

	@Override
	public boolean isBookmarkNode()
	{
		return isBookmarkNode;
	}

	@Override
	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	@Override
	public void addChild(BMNode child)
	{
		children.add(child);
		child.setParent(this);
	}

	@Override
	public void removeChild(BMNode child)
	{
		children.remove(child);
		child.setParent(null);
	}

	@Override
	public BMNode[] getChildren()
	{
		return (BMNode[]) children.toArray(new BMNode[children.size()]);
	}

	@Override
	public boolean hasChildren()
	{
		return children.size() > 0;
	}

	@Override
	public void setParent(BMNode parent)
	{
		this.parent = parent;
	}

	@Override
	public BMNode getParent()
	{
		return parent;
	}

	@Override
	public void removeAllChildren()
	{
		for (BMNode child : getChildren()) {
			TreeUtil.unlink(child);
		}
	}

	@Override
	public BMNode getReference()
	{
		return this;
	}

	@Override
	public boolean hasReference()
	{
		return false;
	}
}
