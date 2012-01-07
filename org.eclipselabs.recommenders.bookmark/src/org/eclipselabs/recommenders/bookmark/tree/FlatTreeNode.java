package org.eclipselabs.recommenders.bookmark.tree;

public class FlatTreeNode
	extends TreeNode
{
	private BMNode ref;

	public FlatTreeNode(BMNode node)
	{
		super(node.getValue(), node.isBookmarkNode(), node.showInFlatModus());
		this.ref = node;
	}

	@Override
	public BMNode getReference()
	{
		return ref;
	}

	@Override
	public boolean hasReference()
	{
		return true;
	}

}
