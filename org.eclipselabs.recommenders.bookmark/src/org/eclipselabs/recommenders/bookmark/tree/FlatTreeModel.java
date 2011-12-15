package org.eclipselabs.recommenders.bookmark.tree;

public class FlatTreeModel
{
	/**
	 * The root node is the highest node in the hierarchy, below the root are
	 * bookmark nodes
	 */
	private FlatTreeNode root = null;

	public FlatTreeModel()
	{
		root = new FlatTreeNode();
	}

	public FlatTreeNode getModelRoot()
	{
		return root;
	}

	public void setModelRoot(FlatTreeNode root)
	{
		this.root = root;
	}

}
