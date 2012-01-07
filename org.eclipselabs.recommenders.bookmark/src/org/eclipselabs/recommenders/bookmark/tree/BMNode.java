package org.eclipselabs.recommenders.bookmark.tree;

public interface BMNode
{
	public void addChild(BMNode child);

	public void removeChild(BMNode child);

	public BMNode[] getChildren();

	public boolean hasChildren();

	public void setParent(BMNode parent);

	public BMNode getParent();

	public boolean showInFlatModus();
	
	public void setShowInFlatModus(boolean state);

	public boolean isBookmarkNode();

	public void setValue(Object value);

	public Object getValue();

	public boolean hasParent();

	public void removeAllChildren();

	public BMNode getReference();

	public boolean hasReference();
}
