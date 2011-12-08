package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.viewers.TreeViewer;

public interface BookmarkView {
	
	public TreeViewer getView();
	
	public boolean requiresSelectionForToggle();
	
	public void updateControls();
	
}
