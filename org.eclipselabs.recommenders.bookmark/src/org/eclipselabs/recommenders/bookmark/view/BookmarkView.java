package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.viewers.TreeViewer;

public interface BookmarkView
{

	public TreeViewer getView();

	public void updateControls();

	public ViewManager getManager();

}
