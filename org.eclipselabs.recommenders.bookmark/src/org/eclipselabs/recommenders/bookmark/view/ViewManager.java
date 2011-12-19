package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;

public interface ViewManager
{

	public ViewPart getViewPart();

	public BookmarkView activateNextView();

	public BookmarkView getActiveBookmarkView();

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

	public void activateFlattenedModus(BMNode parentThatsChildsShallBeFlattened);

	public void deactivateFlattenedModus();

	public boolean isViewFlattened();
	
	public boolean isViewToggled();
}
