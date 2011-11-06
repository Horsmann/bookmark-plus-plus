package org.eclipselabs.recommenders.bookmark.tree.node;

public class BookmarkNode extends TreeNode {
	
	private String comment;

	public BookmarkNode(String text) {
		super(text);
	}
	
	public BookmarkNode(String text, String comment) {
		super(text);
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	

}
