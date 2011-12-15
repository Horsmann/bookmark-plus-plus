package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;

public interface BookmarkView
{

	public TreeViewer getView();

	public TreeModel getModel();

	public TreeModel getFlatModel();

	public boolean requiresSelectionForToggle();

	public void updateControls();

	public ViewManager getManager();

}
