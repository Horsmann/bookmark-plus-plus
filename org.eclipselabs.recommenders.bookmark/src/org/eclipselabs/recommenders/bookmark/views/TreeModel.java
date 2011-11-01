package org.eclipselabs.recommenders.bookmark.views;

import java.util.ArrayList;

public class TreeModel {

	public TreeObject getModel() {
		
		
		TreeParent root = new TreeParent("top");
		TreeObject level1 = new TreeObject("level one");
		root.addChild(level1);
		
		TreeObject level2 = new TreeObject("level two");
		root.addChild(level2);
		
		
		TreeObject level3 = new TreeObject("level three");
		root.addChild(level3);
		
		TreeParent dummy = new TreeParent("");
		dummy.addChild(root);
		
		return dummy;
	}
}

class TreeObject {

	private String name;
	private TreeParent parent;

	public TreeObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

}

class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;

	public TreeParent(String name) {
		super(name);
		children = new ArrayList<TreeObject>();
	}

	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

}
