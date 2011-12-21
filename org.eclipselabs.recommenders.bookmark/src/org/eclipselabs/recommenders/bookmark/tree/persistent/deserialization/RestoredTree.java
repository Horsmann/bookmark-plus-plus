package org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization;

import org.eclipselabs.recommenders.bookmark.tree.BMNode;

public class RestoredTree {
	
	private BMNode root;
	private BMNode [] expanded;
	
	public RestoredTree(BMNode root, BMNode [] expanded){
		this.root = root;
		this.expanded = expanded;
	}

	public BMNode getRoot() {
		return root;
	}

	public BMNode[] getExpanded() {
		return expanded;
	}

}
