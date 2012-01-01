package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;

public interface ViewManager
{

	public ViewPart getViewPart();

	public BookmarkView activateNextView();

	public BookmarkView getActiveBookmarkView();
	
	public ExpandedStorage getExpandedStorage();

	public void activateFlattenedModus(BMNode parentThatsChildsShallBeFlattened);

	public void deactivateFlattenedModus();

	public boolean isViewFlattened();

	public boolean isViewToggled();

	public TreeModel getModel();

	public void saveModelState();

	public TreeModel getFlatModel();

	public void activateToggledView();

	public void activateDefaultView();
}
