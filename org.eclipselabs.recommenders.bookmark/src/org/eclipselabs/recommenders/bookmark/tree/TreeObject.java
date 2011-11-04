package org.eclipselabs.recommenders.bookmark.tree;

public abstract class TreeObject {
	private String name;
	protected TreeNode parent;

	public TreeObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public TreeNode getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}
}
