package org.eclipselabs.recommenders.bookmark.tree;

public class TreeModel
{

	/**
	 * The root node is the highest node in the hierarchy, below the root are
	 * bookmark nodes
	 */
	private BMNode root = null;

	/**
	 * The head node is the node that is currently set as 'root' for the view
	 */
	private BMNode head = null;

	public TreeModel()
	{
		root = new TreeNode("", false, false);
		head = root;
	}

	public void setHeadNode(BMNode node)
	{
		head = node;
	}

	public void resetHeadToRoot()
	{
		head = root;
	}

	public BMNode getModelRoot()
	{
		return root;
	}

	public BMNode getModelHead()
	{
		return head;
	}

	public void setModelRoot(BMNode root)
	{
		this.root = root;
		this.head = this.root;
	}

	public boolean isHeadEqualRoot()
	{
		return root == head;
	}
}
