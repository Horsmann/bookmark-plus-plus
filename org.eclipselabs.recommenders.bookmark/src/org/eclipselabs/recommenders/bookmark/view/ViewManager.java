package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.part.ViewPart;

public interface ViewManager
{

	public ViewPart getViewPart();

	public BookmarkView activateNextView();

	public TreeViewer getActiveViewer();

	/**
	 * Takes the nodes of the currently active view and adds all expanded nodes
	 * to the storage
	 */
	public void addCurrentlyExpandedNodesToStorage();

	/**
	 * All stored nodes are set to expanded for the currently active view
	 */
	public void setStoredExpandedNodesForActiveView();

	public void deleteExpandedNodeFromStorage(Object node);

	public void reinitializeExpandedStorage();

//	public void activateFlattenedView();
//
//	public void deactivateFlattenedView();
//
//	public boolean isViewFlattened();
}
