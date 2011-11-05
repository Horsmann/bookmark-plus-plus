package org.eclipselabs.recommenders.bookmark.tree;

import java.util.ArrayList;

public class TreeNode {
	private TreeNode parent;
	private ArrayList<TreeNode> children;
	private String name;
	private String path;
	private String project;

	public TreeNode(String path) {
		this.name = extractName(path);
		this.project = extractProject(path);
		this.path = path;
		children = new ArrayList<TreeNode>();
	}

	public String getProjectName() {
		return project;
	}

	private String extractProject(String path) {
		int posFirstSlash = path.indexOf("/");
		int posSecondSlash = 0;
		if (posFirstSlash > -1)
			posSecondSlash = path.indexOf("/", posFirstSlash + 1);
		else
			return path;

		if (posSecondSlash > -1) {
			return path.substring(posFirstSlash + 1, posSecondSlash
					- posFirstSlash);
		}
		return path;
	}

	public void removeAllChildren() {
		children = new ArrayList<TreeNode>();
	}

	public void addChild(TreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeNode[] getChildren() {
		return (TreeNode[]) children.toArray(new TreeNode[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	private String extractName(String path) {
		int posLastSlash = path.lastIndexOf("/");
		int posDot = path.lastIndexOf(".");

		if (posDot == -1 || posLastSlash == -1)
			return path;

		String name = path.substring(posLastSlash + 1);

		return name;
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
