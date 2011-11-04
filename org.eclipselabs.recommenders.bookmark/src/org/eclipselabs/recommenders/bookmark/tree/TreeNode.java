package org.eclipselabs.recommenders.bookmark.tree;

import java.util.ArrayList;

public class TreeNode extends TreeObject {
		private ArrayList<TreeNode> children;

		public TreeNode(String name) {
			super(name);
			children = new ArrayList<TreeNode>();
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

}
